import Foundation

struct HomeItem: Identifiable, Hashable {
    // Usamos UUID() por defeito se não vier ID, para o SwiftUI não se queixar
    var id: String = UUID().uuidString
    
    // Campos do Curso
    var coachName: String = ""
    var coachProgram: String = ""
    var whatToExpectProgram: String = ""
    var coachStartDate: String = ""
    var coachDateEnd: String = ""
    var coachPriceOption1: Double = 0.0
    var option1WhatToExpect: String = ""
    var coachPriceOption2: Double = 0.0
    var option2WhatToExpect: String = ""
    var coachDiscount: Double = 0.0
    var coachVacancies: Int = 0
    
    // Links
    var instagramUrl: String = ""
    var facebookUrl: String = ""
    var tiktokUrl: String = ""
    var linkedinUrl: String = ""
    var podcastUrl: String = ""
    var registrationsUrl: String = ""
    
    // Campos de Meditação
    var meditationCirclesDesc: String = ""
    var meditationCirclesType: String = ""
    var meditationCirclesNextSession: String = ""
    var meditationCirclesLocation: String = ""
    var meditationCirclesPrice: Double = 0.0
}
