package id3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JPanel;
import java.awt.BorderLayout;


import javax.swing.JComboBox;

public class ID3_V2 extends JFrame
{

	private static ID3_V2 instance = null;
	
	private static final long serialVersionUID = 1L;	//eclipse me met un warning si je le met pas
	private String nomAttributs[] = null;	// contient les noms des attributs
	private int nbExemples = 0;				// nombre d'exemples
	private int nbAttributs = 0;			// nombre d'attributs
	private Vector valeurAttributs[];		// tableau contenant les valeurs des attributs (string)
	private Vector Exemples = null;			// contient les exemples sous forme de chiffres
	private Noeud root = null;				// noeud racine
	private JPanel jPanel = null;
	private JTree arbre = null;
	
	
	/**
	 *  utilisation du pattern singleton
	 *
	 */
	private ID3_V2() {
		super();
		initialize();
	}
	
	public static ID3_V2 getInstance()
	{
		if(instance==null)
			instance = new ID3_V2();
		return instance;
	}

	private void initialize() {
        this.setSize(new java.awt.Dimension(321,317));
        this.setContentPane(getJPanel());
        this.setTitle("ID3");
        File f = new File(".");
	    File[] listFiles = f.listFiles();
	    Vector listeExemples = new Vector();
	    listeExemples.add("");
	    for(int i = 0;i<listFiles.length;i++)
	    {
	    	if(listFiles[i].isFile() && !listFiles[i].toString().substring(2).contains("."))
	    	{
	    		listeExemples.add(listFiles[i].toString().substring(2));
	    	}
	    }
	    JComboBox jComboBox = new JComboBox(listeExemples);
	    jComboBox.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				try
				{
					ID3_V2.getInstance().goFor(e.getItem().toString());
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});
	    jPanel.add(jComboBox, java.awt.BorderLayout.NORTH);
		
	}
	

	/**
	 * met la machine en route, cette methode est appel�e � chaque changement de selection de fichier d'entr�e
	 * @param fichier
	 * @throws Exception
	 */
	public void goFor(String fichier) throws Exception
	{
		if(fichier.equals("")||fichier==null)
			return;
		readData(fichier);
		createArbre();
		afficherArbre();
	}

	/**
	 * Cette fonction renvoie la valeur num�rique correspondant
	 * � la valeur de l'attribut (string)
	 * @param attribut
	 * @param value 
	 * @return
	 */
	public int valeurAttribut(int attribut,String value) // numéro d'attribut + le nom
	{
		int valeur = valeurAttributs[attribut].indexOf(value);
		if (valeur < 0) //valeur n'existe pas
		{
			valeurAttributs[attribut].addElement(value);//ajouter la valeur au vecteur
			valeur =  valeurAttributs[attribut].size() -1; //après l'ajout de valeur, la valeur s'est la derniere
		}
		return valeur;
	}
	
	/**
	 * Cette fonction ouvre le fichier contenant les exemples et les int�gre au programme
	 * @param nomFichier
	 * @throws Exception
	 */
	public void readData(String nomFichier) throws Exception
	{
		Exemples = new Vector();
		nbExemples = 0;
		/*
		 * ouverture du fichier contenant les exemples
		 */
		FileInputStream in = null;
  		try
		{
			File inputFile = new File(nomFichier);
			in = new FileInputStream(inputFile);
		}
		catch (Exception e)	{return;}

  		BufferedReader bin = new BufferedReader(new InputStreamReader(in) );

		String input;
  		input = bin.readLine();									// premire ligne du fichier contient les noms des differents attributs
  		StringTokenizer tokenizer = new StringTokenizer(input);	
		nbAttributs = tokenizer.countTokens();					
		nomAttributs = new String[nbAttributs];
		for(int i=0;i<nbAttributs;i++)
		{
			nomAttributs[i]=tokenizer.nextToken();				//nomAttributs contient le nom de tous les attributs + le comcept cible
		}
		
		valeurAttributs = new Vector[nbAttributs];
		for (int i = 0; i< nbAttributs; i++)
		{
			valeurAttributs[i]= new Vector();
		}
		
		/*
		 *  recup�ration des donn�es
		 */
		int [] exemple;
		while(true)
		{
			exemple = new int[nbAttributs];
			String value;
			input = bin.readLine();		//nouvel exemple
			if(input == null) break;	// si input = null fin des exemples
			nbExemples++;
			tokenizer = new StringTokenizer(input);
			int nbToken = tokenizer.countTokens();
			for (int attribut = 0; attribut<nbToken;attribut++)
			{
				value = tokenizer.nextToken();
				exemple[attribut] = valeurAttribut(attribut,value);
			}
			Exemples.add(exemple);
		}
		bin.close();
	}
	
	/**
	 * c'est comme le Port-Salut
	 *
	 */
	public void createArbre()
	{		
		root = new Noeud(valeurAttributs,Exemples,nbAttributs,nomAttributs);
		root.generationArbre();
	}

	/**
	 * c'est comme le Port-Salut
	 *
	 */
	public void afficherArbre()
	{
		if(arbre!=null)
			jPanel.remove(arbre);
		DefaultMutableTreeNode racine = root.generationAffichage();
		arbre = new JTree(racine);
		getContentPane().add(arbre,BorderLayout.CENTER);
		this.setVisible(true);

	}
	
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel()
	{
		if (jPanel == null)
		{
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			
		}
		return jPanel;
	}

	/**
     * @param args 
     */
	public static void main(String[] args)throws Exception
	{
		ID3_V2 id3 = ID3_V2.getInstance();
		id3.setVisible(true);
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
