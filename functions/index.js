const functions = require("firebase-functions/v1");
const admin = require("firebase-admin");
admin.initializeApp();

// ==========================================================
// 1. QUANDO ALGUÉM CRIA UMA RESERVA -> AVISA OS ADMINS
// ==========================================================
exports.notifyAdminOnNewBooking = functions.firestore
  .document("bookings/{bookingId}")
  .onCreate((snap, context) => {
    const newData = snap.data();
    const clientName = newData.clientName || "Cliente";
    
    // Tenta obter o serviço, se não existir mete texto genérico
    const service = newData.serviceType || "Serviço";

    const payload = {
      notification: {
        title: "Nova Marcação! 📅",
        body: `${clientName} marcou: ${service}.`,
        sound: "default",
      }
    };

    // Envia para o tópico 'admin_notifications'
    return admin.messaging().sendToTopic("admin_notifications", payload);
  });

// ==========================================================
// 2. QUANDO O ESTADO MUDA -> AVISA O CLIENTE
// ==========================================================
exports.notifyUserOnStatusChange = functions.firestore
  .document("bookings/{bookingId}")
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();

    // Se o estado for igual, não faz nada
    if (before.status === after.status) return null;

    const clientEmail = after.clientEmail;
    
    // Procura o Token do telemóvel do user na coleção 'users'
    const usersSnapshot = await admin.firestore().collection("users")
      .where("email", "==", clientEmail).limit(1).get();

    if (usersSnapshot.empty) {
      console.log("User não encontrado ou sem token");
      return null;
    }

    const userDoc = usersSnapshot.docs[0].data();
    const fcmToken = userDoc.fcmToken;

    if (!fcmToken) return null;

    let title = "Atualização da Reserva";
    let body = "O estado da tua reserva mudou.";

    if (after.status === "confirmed") {
        title = "Reserva Confirmada! ✅";
        body = "A tua marcação foi aceite. Até já!";
    } else if (after.status === "rejected") {
        title = "Reserva Não Aceite ❌";
        body = "Infelizmente não foi possível aceitar esta data.";
    }

    const payload = {
      notification: {
        title: title,
        body: body,
        sound: "default",
      }
    };

    return admin.messaging().sendToDevice(fcmToken, payload);
  });

// ==========================================================
// 3. NOVO: NOTIFICAÇÃO DO CHAT DA COMUNIDADE 💬
// ==========================================================
exports.sendChatNotification = functions.firestore
    .document('community_chat/{messageId}')
    .onCreate(async (snapshot, context) => {
        const messageData = snapshot.data();

        // Validação básica: se não tiver texto, ignora
        if (!messageData.text || !messageData.senderName) {
            console.log('Mensagem incompleta, ignorada.');
            return null;
        }

        const payload = {
            notification: {
                title: `Nova mensagem de ${messageData.senderName}`,
                body: messageData.text,
                sound: "default"
            },
            // Dados extras para ajudar a App a abrir o ecrã certo
            data: {
                screen: "community_chat",
                click_action: "FLUTTER_NOTIFICATION_CLICK"
            }
        };

        // Envia para o tópico "community_chat"
        // (Lembrete: A App Android deve subscrever este tópico no SessionViewModel)
        return admin.messaging().sendToTopic("community_chat", payload);
    });