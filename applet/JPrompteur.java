/** 
 * Titre: JPrompteur
 * Version: 0.2
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

        // Double buffering
        private Graphics ecran;
        private Image _ecran;       
        private Graphics screen;
        
        /** texte en cours */
	int i=0;
        /** texte d'erreur */
	String Error;
	String ligne="";

	public void init() 
	{
                _ecran = createImage(size().width,size().height);
                ecran = _ecran.getGraphics();
		// Recup des paramètres
		titre = getParameter("titre");
		if (titre == null) { titre = "JPrompteur v0.2, Copyright (c) 2001,2002 - Philippe BOUSQUET, cette Applet est sous licence GPL"; }
		String col = getParameter("background");
	    	if (col != null) { bcolor = decodeColor(col); }
		else { bcolor=Color.blue; }
		col = getParameter("foreground");
	    	if (col != null) { fcolor = decodeColor(col); }
		else { fcolor=Color.yellow; }
		// Fichier des messages;
		String nom=getParameter("fichier");
		// Fichier des messages;
		String local=getParameter("local");
		if (nom != null)
		{
                        if (local.compareTo("oui")==0) { nom=getCodeBase().toString() + nom; }
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
			catch (java.io.IOException e) { System.err.println("Probleme d' I/O"); }
			catch (java.lang.SecurityException e) { System.err.println("Probleme de securité"); }
		}

		// Initialisations
		Font font = new java.awt.Font("TimesRoman", Font.PLAIN, 20);
		ecran.setFont(font);
	}

        private void affiche()
        {
                screen.drawImage(_ecran, 0, 0, this);
        }

	public void ecrit() 
	{
    		int y = ecran.getFont().getSize();
                ecran.setColor(bcolor);
 		ecran.fillRect(0,0,size().width,size().height);
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
			FontMetrics fm = ecran.getFontMetrics();
			limite = -1 * (fm.stringWidth(message) + 15);
			x=d.width + 15;
		}
		else
		{
			x =x-5;
		}
		ecran.setColor(fcolor);
		ecran.drawString(message, x, y);
                affiche();
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
		screen = getGraphics();
                Thread me = Thread.currentThread();
		while (prompteur == me) 
		{
			ecrit();
			try {Thread.currentThread().sleep(100); }
			catch (InterruptedException e) {}
		}
	}

	public String getAppletInfo() 
	{
		String retour;
		retour="Titre: JPrompteur\nVersion: 0.2\n"+
		"Description: Permet de faire défiler des messages sur une page\n"+
		"Auteur: Philippe BOUSQUET\n"+
		"Copyright (c) 2001,2002 - Philippe BOUSQUET\n"+
		"Cette Applet est sous licence GENERAL PUBLIC LICENSE\n";
		return retour;
	}

	public String[][] getParameterInfo() 
	{
		String pinfo[][] = 
		{
			{"titre", "string", "Message d'acceuil","ex: 'Bienvenu !!!'"},
			{"fihcier", "string", "Fichier contenant les messages","ex: 'message.txt'"},
 			{"background", "string", "Couleur de fond","ex: '#000000'"},
 			{"foreground", "string", "Couleur du texte","ex: '#0000FF'"},
 			{"local", "string", "Indique si le fichier est sur le local","ex: 'oui'"}
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
