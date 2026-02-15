import Foundation
import FirebaseFirestore

class HomeRepository {
    
    private let db = Firestore.firestore()
    private let docRefString = "home_items/nkC4lOyD6jZSxBw1k37E"
    
    func getHomeItems() async -> [HomeItem] {
        do {
            let document = try await db.document(docRefString).getDocument()
            
            if document.exists, let data = document.data() {
                var finalList: [HomeItem] = []
                
                // --- 1. LER CURSOS ---
                if let rawData = data["courses"] as? [[String: Any]] {
                    for item in rawData {
                        let program = item["coachProgram"] as? String ?? ""
                        
                        // Só processa se tiver nome de programa
                        if !program.isEmpty {
                            let homeItem = HomeItem(
                                id: UUID().uuidString,
                                coachProgram: program,
                                whatToExpectProgram: item["whatToExpectProgram"] as? String ?? "",
                                coachStartDate: item["coachStartDate"] as? String ?? "",
                                coachDateEnd: item["coachDateEnd"] as? String ?? "",
                                // CORREÇÃO: self.parseDouble
                                coachPriceOption1: self.parseDouble(item["coachPriceOption1"]),
                                coachPriceOption2: self.parseDouble(item["coachPriceOption2"]),
                                coachDiscount: self.parseDouble(item["coachDiscount"]),
                                // CORREÇÃO: self.parseInt
                                coachVacancies: self.parseInt(item["coachVacancies"]),
                                instagramUrl: item["instagramUrl"] as? String ?? ""
                            )
                            finalList.append(homeItem)
                        }
                    }
                }
                
                // --- 2. LER MEDITAÇÃO ---
                let medType = data["meditationCirclesType"] as? String ?? ""
                if !medType.isEmpty {
                    let medItem = HomeItem(
                        id: "meditacao_raiz",
                        meditationCirclesDesc: data["meditationCirclesDesc"] as? String ?? "",
                        meditationCirclesType: medType,
                        meditationCirclesNextSession: data["meditationCirclesNextSession"] as? String ?? "",
                        meditationCirclesLocation: data["meditationCirclesLocation"] as? String ?? "",
                        // CORREÇÃO: self.parseDouble
                        meditationCirclesPrice: self.parseDouble(data["meditationCirclesPrice"])
                    )
                    finalList.append(medItem)
                }
                
                return finalList
            } else {
                print("FIREBASE_ERRO: Documento não existe")
                return []
            }
        } catch {
            print("FIREBASE_CRASH: \(error.localizedDescription)")
            return []
        }
    }
    
    // --- FUNÇÕES AUXILIARES (CORRIGIDO: 'func' em vez de 'fun') ---
    
    private func parseDouble(_ value: Any?) -> Double {
        guard let value = value else { return 0.0 }
        
        if let number = value as? NSNumber {
            return number.doubleValue
        }
        if let string = value as? String {
            // Tenta substituir vírgula por ponto para converter
            let formatted = string.replacingOccurrences(of: ",", with: ".")
            return Double(formatted) ?? 0.0
        }
        return 0.0
    }
    
    private func parseInt(_ value: Any?) -> Int {
        guard let value = value else { return 0 }
        
        if let number = value as? NSNumber {
            return number.intValue
        }
        if let string = value as? String {
            return Int(string.trimmingCharacters(in: .whitespaces)) ?? 0
        }
        return 0
    }
}
