//
//  Bookings.swift
//  almanaboca
//
//  Created by Ivo Xavier on 15/02/2026.
//

import Foundation
import FirebaseFirestore

struct Booking: Identifiable, Codable, Hashable {
    // @DocumentID mapeia automaticamente o ID do documento do Firestore
    @DocumentID var id: String?
    
    var date: String        // "dd.MM.yyyy"
    var time: String        // "14:00"
    var serviceType: String
    var clientName: String
    var clientEmail: String
    var clientPhone: String
    var status: String      // "pending", "confirmed", "rejected"
    var timestamp: Int64?   // Para ordenação
    
    // Status Helpers
    var isPending: Bool { status == "pending" }
    var isConfirmed: Bool { status == "confirmed" }
}
