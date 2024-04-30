package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.*;

public class CardManager {
	public static CardLayout cardLayout = new CardLayout();
	public static JPanel cardDisplay = new JPanel(cardLayout);
	
	private static Component topCard = null;
	private static HashMap<String, Component> cardMap = new HashMap<>();
	
	public static final String HOME = "homeCard";
	public static final String LOGIN = "loginCard";
	public static final String ACCOUNT_CREATE = "accountCreationCard";
	public static final String POST_CREATE = "postCreationCard";
	public static final String PROFILE = "profileCard";
	
	public static void add(Component comp, String name) {
		cardMap.put(name, comp);
		cardDisplay.add(comp, name);
	}
	
	public static void show(String name) {
		topCard = cardMap.get(name);
		cardLayout.show(cardDisplay, name);
	}
	
	public static Component getTopCard() {
		return topCard;
	}
}
