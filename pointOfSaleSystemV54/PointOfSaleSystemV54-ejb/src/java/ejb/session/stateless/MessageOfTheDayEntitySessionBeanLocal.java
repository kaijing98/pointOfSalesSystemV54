package ejb.session.stateless;

import entity.MessageOfTheDayEntity;
import java.util.List;
import util.exception.InputDataValidationException;



public interface MessageOfTheDayEntitySessionBeanLocal
{
    MessageOfTheDayEntity createNewMessageOfTheDay(MessageOfTheDayEntity newMessageOfTheDayEntity) throws InputDataValidationException;

    List<MessageOfTheDayEntity> retrieveAllMessagesOfTheDay();
}
