package domain.project;

import domain.card.Card;
import domain.card.CardFactory;
import domain.card.Symbol;
import domain.card.Type;
import domain.user.Dealer;
import domain.user.Player;
import domain.user.Person;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Random;

public class Game {
	private static List<Card> cardSet;
	private Dealer dealer;
	private HashMap<String, Double> nameMoneyMap;
	private List<Player> playerSet;
	private Scanner in;
	private Printer myPrinter;

	public Game() {
		CardFactory cardFactory = new CardFactory();
		cardSet = new ArrayList<Card>();
		cardSet.addAll(cardFactory.create());
		dealer = new Dealer();
		playerSet = new ArrayList<Player>();
		in = new Scanner(System.in);
		myPrinter = new Printer();
	}

	private void run() {
		initialSet();
		giveInitialCard();
		getCardPerPlayer();
		getCardDealer();
		printResult();
		printWinner();
	}

	private void initialSet() {
		inputName();
		inputBettingMoney();
		addPlayer();
	}

	private void inputName() {
		String name;

		try {
			myPrinter.printInputPlayer();
			name = in.next();
			checkNameValidation(name);
		} catch (GameException e) {
			inputName();
		}
	}

	private void checkNameValidation(String name) {
		isStartComma(name);
		isEndComma(name);
		parseName(name);
	}

	private void isStartComma(String name) {
		if (name.startsWith(Constant.COMMA)) {
			myPrinter.printEmptyPlayerName();
			throw new GameException();
		}
	}

	private void isEndComma(String name) {
		if (name.endsWith(Constant.COMMA)) {
			myPrinter.printEmptyPlayerName();
			throw new GameException();
		}
	}

	private void parseName(String name) {
		nameMoneyMap = new HashMap<String, Double>();
		String[] playerName = name.split(Constant.COMMA);

		for (String s : playerName) {
			isEmptyPlayerName(s);
		}
		checkPlayerSize(playerName);
		addName(playerName);
	}

	private void isEmptyPlayerName(String name) {
		if (name.equals(Constant.EMPTY)) {
			myPrinter.printEmptyPlayerName();
			throw new GameException();
		}
	}

	private void checkPlayerSize(String[] playerName) {
		int size = playerName.length;

		checkSizeUnderTwo(size);
		checkSizeOverEight(size);
	}

	private void checkSizeUnderTwo(int size) {
		if (size < Constant.TWO) {
			myPrinter.printPlayerSizeUnderTwo();
			throw new GameException();
		}
	}

	private void checkSizeOverEight(int size) {
		if (size > Constant.EIGHT) {
			myPrinter.printPlayerSizeOverEight();
			throw new GameException();
		}
	}

	private void addName(String[] playerName) {
		for (String s : playerName) {
			nameMoneyMap.put(s, 0d);
		}
	}

	private void inputBettingMoney() {
		for (String s : nameMoneyMap.keySet()) {
			setBettingMoneyPerPlayer(s);
		}
	}

	private void setBettingMoneyPerPlayer(String name) {
		String bettingMoney;

		try {
			myPrinter.printInputBettingMoney(name);
			bettingMoney = in.next();
			checkBettingMoneyValidation(name, bettingMoney);
		} catch (GameException e) {
			setBettingMoneyPerPlayer(name);
		}
	}

	private void checkBettingMoneyValidation(String name, String bettingMoney) {
		isNum(bettingMoney);
		addBettingMoney(name, bettingMoney);
	}

	private void isNum(String bettingMoney) {
		for (char c : bettingMoney.toCharArray()) {
			checkAscii(c);
		}
	}

	private void checkAscii(char c) {
		if (!(c >= Constant.ASCII_ZERO && c <= Constant.ASCII_NINE)) {
			myPrinter.printInputNumber();
			throw new GameException();
		}
	}

	private void addBettingMoney(String name, String bettingMoney) {
		nameMoneyMap.put(name, convertBettingMoney(bettingMoney));
	}

	private double convertBettingMoney(String bettingMoney) {
		double money = Double.parseDouble(bettingMoney);

		if (money == 0) {
			myPrinter.printNonZeroBettingMoney();
			throw new GameException();
		}
		return money;
	}

	private void addPlayer() {
		for (String name : nameMoneyMap.keySet()) {
			playerSet.add(new Player(name, nameMoneyMap.get(name)));
		}
	}

	private void giveInitialCard() {
		myPrinter.printInitialCard(makePlayerNameString());
		giveInitialCardToDealer();
		giveInitialCardToPlayer();
		printDealerCard();
		printPlayerSetCard();
	}

	private String makePlayerNameString() {
		ArrayList<String> nameSet = new ArrayList<String>();
		for (Player p : playerSet) {
			nameSet.add(p.getName());
		}
		return String.join(", ", nameSet);
	}

	private Card makeRandomCard() {
		Random ran = new Random();
		Symbol[] symbols = Symbol.values();
		Type[] types = Type.values();
		Card card;

		do {
			card = new Card(symbols[ran.nextInt(Constant.SYMBOL_COUNT)], types[ran.nextInt(Constant.TYPE_COUNT)]);
		} while (!checkCardInCardSet(card));
		removeCardInCardSet(card);
		return card;
	}

	private boolean checkCardInCardSet(Card card) {
		if (cardSet.contains(card)) {
			return true;
		}
		return false;
	}

	private void removeCardInCardSet(Card card) {
		cardSet.remove(card);
	}

	private void giveInitialCardToDealer() {
		for (int i = 0; i < 2; i++) {
			dealer.addCard(makeRandomCard());
		}
	}

	private void giveInitialCardToPlayer() {
		for (Player p : playerSet) {
			giveTwoCardToPlayer(p);
		}
	}

	private void giveTwoCardToPlayer(Player player) {
		for (int i = 0; i < 2; i++) {
			player.addCard(makeRandomCard());
		}
	}

	private void printDealerCard() {
		myPrinter.printDealer();
		myPrinter.printCard(dealer.getCardString());
	}

	private void printPlayerSetCard() {
		for (Player p : playerSet) {
			printPlayerCard(p);
		}
	}

	private void printPlayerCard(Player player) {
		myPrinter.printPlayerNameWithCard(player.getName());
		myPrinter.printCard(player.getCardString());
	}

	private void getCardPerPlayer() {
		myPrinter.printNewLine();
		for (Player p : playerSet) {
			chooseGetCard(p);
		}
	}

	private void chooseGetCard(Player player) {
		String answer;

		do {
			myPrinter.printChooseCard(player.getName());
			answer = in.next();
		} while (isGetAnotherCard(player, answer) && checkPlayerCardSum(player));
		myPrinter.printNewLine();
	}

	private boolean isGetAnotherCard(Player player, String answer) {
		try {
			checkAnswerValidation(answer);
			return returnByAnswer(player, answer);
		} catch (GameException e) {
			return true;
		}
	}

	private void checkAnswerValidation(String answer) {
		if (!(answer.equals(Constant.YES) || answer.equals(Constant.NO))) {
			myPrinter.printNotAllowedAnswer();
			throw new GameException();
		}
	}

	private boolean returnByAnswer(Player player, String answer) {
		if (answer.equals(Constant.YES)) {
			player.addCard(makeRandomCard());
			printPlayerCard(player);
			return true;
		}
		return false;
	}

	private boolean checkPlayerCardSum(Player player) {
		if (player.getCardSum() <= Constant.BLACKJACK) {
			return true;
		}
		myPrinter.printStopGetCard(player.getName());
		return false;
	}

	private void getCardDealer() {
		if (dealer.isContainAce() == true) {
			checkDealerSumWithAce();
			return;
		}
		checkDealerSum();
	}

	private void checkDealerSumWithAce() {
		if (dealer.getCardSumWithAce() == Constant.BLACKJACK) {
			myPrinter.printDealerStopGetCard();
			return;
		}
		checkDealerSum();
	}

	private void checkDealerSum() {
		if (dealer.getCardSum() <= Constant.DEALER_PICK) {
			myPrinter.printDealerGetCard();
			dealer.addCard(makeRandomCard());
			return;
		}
		myPrinter.printDealerStopGetCard();
	}

	private void printResult() {
		myPrinter.printNewLine();
		printDealerResult();
		printPlayerSetResult();
	}

	private void printDealerResult() {
		myPrinter.printDealer();
		myPrinter.printCard(dealer.getResultString());
	}

	private void printPlayerSetResult() {
		for (Player p : playerSet) {
			printPlayerResult(p);
		}
	}

	private void printPlayerResult(Player player) {
		myPrinter.printPlayerNameWithCard(player.getName());
		myPrinter.printCard(player.getResultString());
	}

	private void printWinner() {
		myPrinter.printPrizeResult();
		if (dealer.determineScore() > Constant.BLACKJACK) {
			allPlayerWin();
		}
		if (dealer.determineScore() == Constant.BLACKJACK) {
			dealerWin();
		}
		if (dealer.determineScore() < Constant.BLACKJACK) {
			checkWinPerson();
		}
	}

	private void allPlayerWin() {
		myPrinter.printDealer();
		myPrinter.printZeroPrize();
		for (Player p : playerSet) {
			myPrinter.printPlayerName(p.getName());
			myPrinter.printBettingMoney(p.getBettingMoney());
		}
	}

	private void dealerWin() {
		double dealerMoney = 0d;

		for (Player p : playerSet) {
			dealerMoney = dealerMoney + checkBlackJack(p);
		}
		printDealerWin(dealerMoney);
	}

	private void printDealerWin(double dealerMoney) {
		myPrinter.printDealer();
		myPrinter.printBettingMoney(dealerMoney);
		printBlackJackPlayer();
	}

	private void printBlackJackPlayer() {
		for (Player p : playerSet) {
			myPrinter.printPlayerName(p.getName());
			myPrinter.printBettingMoney(p.getBettingMoney() * p.getRate());
		}
	}

	private double checkBlackJack(Player player) {
		if (player.determineScore() == dealer.determineScore()) {
			player.setRate(Constant.WIN_RATE);
			return player.getBettingMoney();
		}
		player.setRate(Constant.LOOSE_RATE);
		if (player.determineScore() > dealer.determineScore()) {
			return player.getBettingMoney();
		}
		return 0d;
	}

	private void checkWinPerson() {
		int minScore = getMinScore();
		
		if (minScore == dealer.calculateMin()) {
			dealerWin();
			return;
		}
		calculateMoney(minScore);
	}

	private int getMinScore() {
		int min = 100;
		
		min = isMin(dealer.calculateMin(), min);
		for (Player p : playerSet) {
			min = isMin(p.calculateMin(), min);
		}
		return min;
	}
	
	private int isMin(int num1, int num2) {
		if (num1 <= num2)
			return num1;
		return num2;
	}
	
	private void calculateMoney(int min) {
		double dealerMoney = 0;
		
		for (Player p : playerSet) {
			setPlayerRate(p, min);
			dealerMoney = dealerMoney + addDealerMoney(p);
		}
		printDealerAndPlayer(dealerMoney);
	}
	
	private void setPlayerRate(Player player, int min) {
		if (player.calculateMin() > min) {
			player.setRate(Constant.LOOSE_RATE);
			return;
		}
		if (player.calculateMin() == min) {
			checkCardSetSize(player, min);
		}
	}
	
	private void checkCardSetSize(Player player, int min) {
		if (player.getCardSize() == Constant.TWO) {
			player.setRate(Constant.BONUS_RATE);
			return;
		}
		player.setRate(Constant.WIN_RATE);
	}
	
	private double addDealerMoney(Player p) {
		if (p.getRate() == Constant.LOOSE_RATE) {
			return 0d;
		}
		return p.getBettingMoney() * p.getRate();
	}
	
	private void printDealerAndPlayer(double dealerMoney) {
		myPrinter.printDealer();
		myPrinter.printBettingMoney(dealerMoney);
		for (Player p : playerSet) {
			myPrinter.printPlayerName(p.getName());
			myPrinter.printBettingMoney(p.getBettingMoney() * p.getRate());
		}
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.run();
	}
}
