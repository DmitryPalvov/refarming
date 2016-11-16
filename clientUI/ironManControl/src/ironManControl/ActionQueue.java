package ironManControl;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

class ActionQueueElement implements Delayed{//<ActionQueueElement>
	
	/**
	 * Time when to act
	 * */
	public long actionTimeMillis;
	/**
	 * Command index
	 * */
	public short actionId;
	/**
	 * Command value
	 * */
	public char actionValue;

	public ActionQueueElement(long delayMillis, short actionId, char actionValue) {
		super();
		this.actionTimeMillis = System.currentTimeMillis() + delayMillis;
		this.actionId = actionId;
		this.actionValue = actionValue;
	}

	@Override
	public int compareTo(Delayed o) {
        if (this.actionTimeMillis < ((ActionQueueElement) o).actionTimeMillis) {
            return -1;
        }
        if (this.actionTimeMillis > ((ActionQueueElement) o).actionTimeMillis) {
            return 1;
        }
        return 0;
		
	}

	@Override
	public long getDelay(TimeUnit unit) {
		long diff = actionTimeMillis - System.currentTimeMillis();
		return unit.convert(diff, TimeUnit.MILLISECONDS);
	}
}

public class ActionQueue extends DelayQueue<ActionQueueElement> {
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	
	public void clearActions(short actionId) {
		this.removeIf(new Predicate<ActionQueueElement>() {

			@Override
			public boolean test(ActionQueueElement t) {
				return t.actionId == actionId;
			}
		});
	}

}
