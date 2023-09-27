package com.yungert.treinplanner.presentation.utils

enum class TransferType(val value: String) {
    CROSS_PLATFORM("CROSS_PLATFORM"),
    TRANSFER_TIME("TRANSFER_TIME"),
    IMPOSSIBLE_TRANSFER("IMPOSSIBLE_TRANSFER");

    companion object {
        fun fromValue(value: String): TransferType? {
            return values().find { it.value == value }
        }
    }
}

enum class WarningType(val value: String) {
    WARNING("warning"),
    DISRUPTION("DISRUPTION"),
    MAINTENANCE("MAINTENANCE"),
    ERROR("error"),
    ALTERNATIVE_TRANSPORT("ALTERNATIVE_TRANSPORT");

    companion object {
        fun fromValue(value: String): WarningType? {
            return values().find { it.value == value }
        }
    }
}

enum class CrowdForecast(val value: String) {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),
    UNKNOWN("UNKNOWN"),
    onbekend("onbekend");

    companion object {
        fun fromValue(value: String): CrowdForecast? {
            return values().find { it.value == value }
        }
    }
}

enum class TripStatus(val value: String) {
    CANCELLED("CANCELLED"),
    CHANGE_NOT_POSSIBLE("CHANGE_NOT_POSSIBLE"),
    ALTERNATIVE_TRANSPORT("ALTERNATIVE_TRANSPORT"),
    DISRUPTION("DISRUPTION"),
    MAINTENANCE("MAINTENANCE"),
    UNCERTAIN("UNCERTAIN"),
    REPLACEMENT("REPLACEMENT"),
    ADDITIONAL("ADDITIONAL"),
    SPECIAL("SPECIAL"),
    NORMAL("NORMAL");

    companion object {
        fun fromValue(value: String): TripStatus? {
            return TripStatus.values().find { it.value == value }
        }
    }
}

enum class TravelType(val value: String) {
    PUBLIC_TRANSIT("PUBLIC_TRANSIT"),
    WALK("WALK"),
    TRANSFER("TRANSFER"),
    BIKE("BIKE"),
    CAR("CAR"),
    KISS("KISS"),
    TAXI("TAXI"),
    UNKNOWN("UNKNOWN");

    companion object {
        fun fromValue(value: String): TravelType? {
            return TravelType.values().find { it.value == value }
        }
    }
}

enum class ShorterStockClassificationType(val value: String) {
    BUSY("BUSY"),
    FALSE("FALSE"),
    EXTRA_BUSY("EXTRA_BUSY");

    companion object {
        fun fromValue(value: String): ShorterStockClassificationType? {
            return ShorterStockClassificationType.values().find { it.value == value }
        }
    }
}

enum class PrimaryMessageType(val value: String) {
    TRIP_CANCELLED("TRIP_CANCELLED"),
    LEG_CANCELLED("LEG_CANCELLED"),
    LEG_TRANSFER_IMPOSSIBLE("LEG_TRANSFER_IMPOSSIBLE"),
    ALTERNATIVE_TRANSPORT("ALTERNATIVE_TRANSPORT"),
    DISRUPTION("DISRUPTION"),
    MAINTENANCE("MAINTENANCE"),
    REPLACEMENT("REPLACEMENT"),
    ADDITIONAL("ADDITIONAL");

    companion object {
        fun fromValue(value: String): PrimaryMessageType? {
            return PrimaryMessageType.values().find { it.value == value }
        }
    }
}

enum class MessageType(val value: String) {
    MAINTENANCE("MAINTENANCE"),
    DISRUPTION("DISRUPTION"),
    CALAMITY("CALAMITY"),
    UNKNOWM("UNKNOWM");

    companion object {
        fun fromValue(value: String?): MessageType? {
            return MessageType.values().find { it.value == value }
        }
    }
}

enum class MessagePhaseType(val value: String) {
    PHASE_1A("PHASE_1A"),
    PHASE_1B("PHASE_1B"),
    PHASE_2("PHASE_2"),
    PHASE_3("PHASE_3"),
    PHASE_4("PHASE_4"),
    PHASE_5("PHASE_5");


    companion object {
        fun fromValue(value: String): MessagePhaseType? {
            return MessagePhaseType.values().find { it.value == value }
        }
    }
}

enum class NoteType(val value: String) {
    UNKNOWN("UNKNOWN"),
    ATTRIBUTE("ATTRIBUTE"),
    INFOTEXT("INFOTEXT"),
    REALTIME("REALTIME"),
    TICKET("TICKET"),
    HINT("HINT");


    companion object {
        fun fromValue(value: String): NoteType? {
            return NoteType.values().find { it.value == value }
        }
    }
}

enum class NoteCategoryType(val value: String) {
    PLATFORM_INFORMATION("PLATFORM_INFORMATION"),
    OVERCHECK_INSTRUCTION("OVERCHECK_INSTRUCTION"),
    UNKNOWN("UNKNOWN");

    companion object {
        fun fromValue(value: String): NoteCategoryType? {
            return NoteCategoryType.values().find { it.value == value }
        }
    }
}

enum class StopStatusType(val value: String) {
    ORIGIN("ORIGIN"),
    SPLIT("SPLIT"),
    STOP("STOP"),
    PASSING("PASSING"),
    COMBINE("COMBINE"),
    DESTINATION("DESTINATION"),
    STOP_CHANGED_ORIGIN("STOP_CHANGED_ORIGIN"),
    STOP_CHANGED_DESTINATION("STOP_CHANGED_DESTINATION");

    companion object {
        fun fromValue(value: String): StopStatusType? {
            return StopStatusType.values().find { it.value == value }
        }
    }
}

enum class StopKindType(val value: String) {
    DEPARTURE("DEPARTURE"),
    ARRIVAL("ARRIVAL"),
    TRANSFER("TRANSFER");

    companion object {
        fun fromValue(value: String): StopKindType? {
            return StopKindType.values().find { it.value == value }
        }
    }
}




