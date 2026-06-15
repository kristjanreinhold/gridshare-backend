package ee.gridshare.domain;

/** Booking state machine — spec §6. */
public enum BookingStatus {
    PENDING_HOST,
    PENDING_PAYMENT,
    CONFIRMED,
    COMPLETED,
    RATED,
    DECLINED,
    EXPIRED,
    PAYMENT_EXPIRED,
    CANCELLED
}
