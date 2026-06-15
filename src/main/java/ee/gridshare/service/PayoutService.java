package ee.gridshare.service;

import ee.gridshare.domain.Payout;
import ee.gridshare.repo.PayoutRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PayoutService {

    private final PayoutRepository payouts;
    private final CurrentHost currentHost;

    public PayoutService(PayoutRepository payouts, CurrentHost currentHost) {
        this.payouts = payouts;
        this.currentHost = currentHost;
    }

    @Transactional(readOnly = true)
    public List<Payout> myPayouts() {
        return payouts.findByBookingListingHostOrderByCreatedAtDesc(currentHost.get());
    }
}
