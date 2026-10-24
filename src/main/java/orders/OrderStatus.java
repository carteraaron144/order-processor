package orders;

public enum OrderStatus {
    PENDING,
    FULFILLED,
    REJECTED,
    FAILED      // processing crashed on this order too many times; needs a human
}