import Foundation

struct HomeItem: Identifiable, Codable, Hashable {
    // Definimos valores padrão para igualar o comportamento do Kotlin
    var id: String = ""
    var coachName: String = ""
    var coachProgram: String = ""
    var whatToExpectProgram: String = ""
    var mentorName: String = ""
    var mentorHistory: String = ""
    var mentorTags: String = ""
    var coachDateStart: String = ""
    var coachDateEnd: String = ""
    
    // Kotlin Double = Swift Double
    var coachPriceOption1: Double = 0.00
    var option1WhatToExpect: String = ""
    var coachPriceOption2: Double = 0.00
    var option2WhatToExpect: String = ""
    var coachDiscount: Double = 0.00
    
    // Kotlin Int = Swift Int
    var coachVacancies: Int = 0
    
    // URLs
    var instagramUrl: String = ""
    var facebookUrl: String = ""
    var tiktokUrl: String = ""
    var linkedinUrl: String = ""
    var podcastUrl: String = ""
    var registrationsUrl: String = ""
    
    // Helper calculado (opcional, mas útil para o UI)
    // Calcula o preço com desconto se houver
    var discountedPrice: Double {
        if coachDiscount > 0 {
            return coachPriceOption1 * (1 - (coachDiscount / 100))
        }
        return coachPriceOption1
    }
}
