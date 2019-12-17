package domain.user;

import domain.card.Card;
import domain.project.Constant;

import java.util.ArrayList;
import java.util.List;

public class Person {
	private final List<Card> cards = new ArrayList<>();

	public Person() {
	}

	public void addCard(Card card) {
		cards.add(card);
	}

	public String getCardString() {
		String cardString = "";
		ArrayList<String> cardStringSet = new ArrayList<String>();

		for (Card c : cards) {
			cardStringSet.add(c.makeCardString());
		}
		cardString = String.join(", ", cardStringSet);
		return cardString;
	}

	public String getResultString() {
		return getCardString() + Constant.RESULT + makeResult();
	}

	private String makeResult() {
		if (isContainAce() == true && getCardSumWithAce() == Constant.BLACKJACK) {
			return Integer.toString(getCardSumWithAce());
		}
		if (isContainAce() == true && getCardSumWithAce() <= 21 && getCardSumWithAce() >= getCardSum()) {
			return Integer.toString(getCardSumWithAce());
		}
		return Integer.toString(getCardSum());
	}

	public boolean isContainAce() {
		boolean ace = false;

		for (Card c : cards) {
			ace = checkAce(c, ace);
		}
		return ace;
	}

	private boolean checkAce(Card card, boolean ace) {
		if (ace == true) {
			return true;
		}
		if (card.getSymbolName().equals(Constant.ACE)) {
			return true;
		}
		return false;
	}

	public int getCardSum() {
		int cardSum = 0;

		for (Card c : cards) {
			cardSum = cardSum + c.getSymbolScore();
		}
		return cardSum;
	}

	public int getCardSumWithAce() {
		return getCardSum() + Constant.PLUS_ACE;
	}
}
