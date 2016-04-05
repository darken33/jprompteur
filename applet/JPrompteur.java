/** 
 * Titre: JPrompteur
 * Version: 0.1
 * Description: Permet de faire défiler des messages d'information sur une page
 * Creation: 26/09/2001
 * Modification: 26/09/2001
 * Auteur: Philippe BOUSQUET
 * Copyright (c) 2001 - Philippe BOUSQUET
 * Cette Applet est sous licence GENERAL PUBLIC LICENSE 
 */
import java.awt.*;
import java.lang.Integer;
import java.lang.Math;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Vector;

public class JPrompteur extends java.applet.Applet implements Runnable
{
	Thread prompteur = null; 
	/** Titre d'acueil */
	String titre;
	/** Texte à afficher */
	Vector Vect=new Vector();
	/** Message en cours */
	String message="";
	/** Position du texte */
	int x=-1;
	/** Limite gauche du texte */
	int limite=0;
	/** Passage : 0=titre 1=texte */
	int pass;                      
	/** Nombre de lignes du fichier texte */
	int nblignes=0;           
	/** Couleur du fond */
	Color bcolor;        
	/** Couleur du texte */
	Color fcolor;
       /** texte en cours */
	int i=0;
       /** texte d'erreur */
	String Error;
	String ligne="";

	public void init() 
	{
		// Recup des paramètres
		titre = getParameter("titre");
		if (titre == null) { titre = "JPrompteur v0.1, Copyright (c) 2001 - Philippe BOUSQUET, cette Applet est sous licence GPL"; }
		String col = getParameter("background");
	    	if (col != null) { bcolor = decodeColor(col); }
		else { bcolor=Color.blue; }
		col = getParameter("foreground");
	    	if (col != null) { fcolor = decodeColor(col); }
		else { fcolor=Color.yellow; }
		// Fichier des messages;
		String nom=getParameter("fichier");
		if (nom != null)
		{
			try
			{
				URL filein = new URL(nom);
				BufferedReader fichier = new BufferedReader(new InputStreamReader(filein.openStream()));
				ligne=fichier.readLine();
				nblignes=0;
				String ligneprec="";
				while ((ligne != null) && (!ligne.equals(ligneprec)))
				{
					if (ligne.length()>0) Vect.insertElementAt((Object)ligne,nblignes++);
					ligneprec=ligne;
					ligne=fichier.readLine();
				}
				fichier.close();
			} 
			catch (java.io.IOException e) { Error="Probleme d' I/O"; }
			catch (java.lang.SecurityException e) { Error="Probleme de securité"; }
		}

		// Initialisations
 		setBackground(bcolor);
		Font font = new java.awt.Font("TimesRoman", Font.PLAIN, 20);
		setFont(font);
	}

	public void paint(Graphics g) 
	{
    		int y = g.getFont().getSize();
		if (x<limite) 
		{ 
			if ((message.equals(titre)) && (nblignes > 0))
			{
				message=(String)Vect.elementAt(i);
				i++;
				if (i>=nblignes) i=0;
			}
			else message = titre; 
		    Dimension d = getSize();
			FontMetrics fm = g.getFontMetrics();
			limite = -1 * (fm.stringWidth(message) + 15);
			x=d.width + 15;
		}
		else
		{
			x =x-5;
		}
		g.setColor(fcolor);
		g.drawString(message, x, y);
	}

	public void start() 
	{
		prompteur = new Thread(this);
		prompteur.start();
	}

	public synchronized void stop() 
	{
		prompteur = null;
	}

	public void run() 
	{
		Thread me = Thread.currentThread();
		while (prompteur == me) 
		{
			repaint();
			try {Thread.currentThread().sleep(100); }
			catch (InterruptedException e) {}
		}
	}

	public String getAppletInfo() 
	{
		String retour;
		retour="Titre: JPrompteur\nVersion: 0.1\n"+
		"Description: Permet de faire défiler des messages sur une page\n"+
		"Auteur: Philippe BOUSQUET\n"+
		"Copyright (c) 2001 - Philippe BOUSQUET\n"+
		"Cette Applet est sous licence GENERAL PUBLIC LICENSE\n";
		return retour;
	}

	public String[][] getParameterInfo() 
	{
		String pinfo[][] = 
		{
			{"titre", "string", "Message d'acceuil"},
			{"fihcier", "string", "Fichier contenant les messages"},
 			{"background", "string", "Couleur de fond"},
 			{"foreground", "string", "Couleur du texte"}
		};
		return pinfo;
	}

	Color decodeColor(String s) 
	{
		int val = 0;
		try 
		{
	    		if (s.startsWith("0x")) { val = Integer.parseInt(s.substring(2), 16); } 
			else if (s.startsWith("#")) { val = Integer.parseInt(s.substring(1), 16); } 
			else if (s.startsWith("0") && s.length() > 1) {	val = Integer.parseInt(s.substring(1), 8); } 
			else { val = Integer.parseInt(s, 10); }
	    		return new Color(val);
		} 
		catch (NumberFormatException e) { return null; }
	}

}
