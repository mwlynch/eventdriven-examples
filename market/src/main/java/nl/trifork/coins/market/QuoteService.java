package nl.trifork.coins.market;

import nl.trifork.coins.coreapi.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.axonframework.eventhandling.GenericEventMessage.asEventMessage;

@Service
public class QuoteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteService.class);

    @Autowired
    MarketService marketService;

    @Autowired
    EventBus eventBus;


    @Autowired
    QueryUpdateEmitter queryUpdateEmitter;

    @CommandHandler
    public void generateQuote(GenerateQuoteCommand command) {
        LOGGER.info("GenerateQuoteCommand {}",command);
        this.marketService
                .retrieveMarketData(command.getId())
                .map(coin -> coin.getPrice())
                .doOnError(error ->
                        this.eventBus.publish(asEventMessage(new GenerateQuoteFailedEvent(command.getId(), error.getMessage()))))
                .subscribe(price ->
                        this.eventBus.publish(asEventMessage(new QuoteGeneratedEvent(command.getId(), command.getUserId(), command.getFromCurrency(), command.getToCurrency(), command.getAmount(), command.getAmount().multiply(price)))));
    }

    @EventHandler
    public void on(QuoteGeneratedEvent event) {
        LOGGER.info("QuoteGeneratedEvent {}",event);
        queryUpdateEmitter.emit(GetQuoteQuery.class, query -> query.getId().equals(event.getId()), new GetQuoteResponse(new QuoteDto(event.getId(), event.getFromCurrency(), event.getToCurrency(), event.getAmount(), event.getPrice()), null));
    }

    @EventHandler
    public void on(GenerateQuoteFailedEvent event) {
        LOGGER.info("GenerateQuoteFailedEvent {}",event);
        queryUpdateEmitter.emit(GetQuoteQuery.class, query -> query.getId().equals(event.getId()), new GetQuoteResponse(null, event.getMessage()));
    }

    @QueryHandler
    public GetQuoteResponse getQuote(GetQuoteQuery query){
        //normally implemented by looking up the quote in the read model
        return new GetQuoteResponse(null,null);
    }
}
