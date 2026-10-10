package com.thelazybattley.dinkspace.feature.courts.data.remote

/**
 * Sample courts for development. They cover the optional fields on purpose:
 * some venues have no Messenger, email, logo, photos or rate.
 */
object DummyCourts {

    private fun logo(seed: String) = "https://picsum.photos/seed/$seed-logo/300/300"

    private fun photos(seed: String, count: Int) =
        List(count) { index -> "https://picsum.photos/seed/$seed-$index/640/440" }

    val all: List<CourtDto> = listOf(
        CourtDto(
            id = "riverside-park",
            name = "Riverside Park Courts",
            area = "Westside",
            address = "120 River Rd, Westside",
            courtCount = 6,
            setting = "outdoor",
            openingHour = 6,
            closingHour = 22,
            phoneNumber = "+1 (555) 014-2290",
            messengerUrl = "https://m.me/riversidepickleball",
            email = "bookings@riversidecourts.example",
            logoUrl = logo("riverside"),
            photoUrls = photos("riverside", 4),
            hourlyRate = 12.0,
        ),
        CourtDto(
            id = "northgate-rec",
            name = "Northgate Rec Center",
            area = "Northgate",
            address = "45 Northgate Ave, Northgate",
            courtCount = 4,
            setting = "indoor",
            openingHour = 7,
            closingHour = 21,
            phoneNumber = "+1 (555) 013-8841",
            email = "frontdesk@northgaterec.example",
            logoUrl = logo("northgate"),
            photoUrls = photos("northgate", 3),
            hourlyRate = 18.0,
        ),
        CourtDto(
            id = "elm-street",
            name = "Elm Street Courts",
            area = "Old Town",
            address = "8 Elm St, Old Town",
            courtCount = 8,
            setting = "outdoor",
            openingHour = 5,
            closingHour = 23,
            phoneNumber = "+1 (555) 017-3302",
            messengerUrl = "https://m.me/elmstreetpb",
        ),
        CourtDto(
            id = "westfield-sports",
            name = "Westfield Sports Club",
            area = "Westfield",
            address = "300 Westfield Blvd, Westfield",
            courtCount = 6,
            setting = "indoor",
            openingHour = 8,
            closingHour = 22,
            phoneNumber = "+1 (555) 012-9076",
            messengerUrl = "https://m.me/westfieldsportsclub",
            email = "courts@westfieldsc.example",
            logoUrl = logo("westfield"),
            photoUrls = photos("westfield", 6),
            hourlyRate = 25.0,
        ),
        CourtDto(
            id = "harbor-view",
            name = "Harbor View Pickleball",
            area = "Harbor District",
            address = "12 Pier Ln, Harbor District",
            courtCount = 3,
            setting = "outdoor",
            openingHour = 6,
            closingHour = 20,
            phoneNumber = "+1 (555) 019-4415",
            logoUrl = logo("harbor"),
        ),
        CourtDto(
            id = "summit-athletic",
            name = "Summit Athletic Center",
            area = "Hillcrest",
            address = "77 Summit Dr, Hillcrest",
            courtCount = 10,
            setting = "indoor",
            openingHour = 5,
            closingHour = 24,
            phoneNumber = "+1 (555) 016-2087",
            email = "play@summitathletic.example",
            photoUrls = photos("summit", 2),
            hourlyRate = 20.0,
        ),
        CourtDto(
            id = "maple-grove",
            name = "Maple Grove Park",
            area = "Eastside",
            address = "1 Maple Grove Way, Eastside",
            courtCount = 2,
            setting = "outdoor",
            openingHour = 7,
            closingHour = 19,
            phoneNumber = "+1 (555) 011-6650",
            messengerUrl = "https://m.me/maplegrovepb",
            logoUrl = "https://example.invalid/broken-logo.png",
        ),
    )
}
