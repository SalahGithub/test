package id3;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;



public class Noeud
{
	private int attribut = -1;      // -1 si les fils ont des valeurs differentes
	private int valeur = -1;		// valeur num�rique de l'attribut p�re, qui enendre ce noeud
	private Noeud pere = null;		// c'est comme le Port-Salut
	private Noeud []fils = null;	// c'est comme le Port-Salut
	private String nomAttributs[];
	private int nbAttributs = 0;			
	private Vector valeurAttributs[];		
	private Vector Exemples = new Vector();  	// contient les sous-exemples des exemples du noeud p�re 
												// correspondant � la valeur qui engendra ce noeud	
	private int etiquette = -1;	// -1 il ne s'agit pas d'une feuille
												// sinon c'est une feuille et etiquette repr�sente la valeur du concept cible
	

	/**
	 * 
	 * @param attributs2
	 * @param exemples
	 * @param attributs
	 * @param exemples2
	 */
	public Noeud(Vector[] attributs2,Vector exemples, int attributs, String[] nomAttributs)
	{
		super();
		Exemples = exemples;
		nbAttributs = attributs;
		valeurAttributs = attributs2;
		this.nomAttributs = nomAttributs;
	}

	
	public DefaultMutableTreeNode generationAffichage()
	{
		DefaultMutableTreeNode noeud = null;
		if(pere != null && etiquette!=-1)  // feuille
		{
			noeud = new DefaultMutableTreeNode(valeurAttributs[pere.attribut].elementAt(valeur)+" : "+
												nomAttributs[nbAttributs-1]+" = "+
												valeurAttributs[attribut].elementAt(etiquette));
			return noeud;
		}
		if(pere!=null && etiquette==-1)   // noeud central
		{
			noeud = new DefaultMutableTreeNode(valeurAttributs[pere.attribut].elementAt(valeur)+" : "+
					nomAttributs[attribut]);
			for(int i = 0;i<fils.length;i++)
			{
				if(fils[i]!=null)
					noeud.add(fils[i].generationAffichage());
			}
			return noeud;
		}
		if(pere==null && etiquette == -1) //racine
		{
			noeud = new DefaultMutableTreeNode(nomAttributs[attribut]);
			for(int i = 0;i<fils.length;i++)
			{
				if(fils[i]!=null)
					noeud.add(fils[i].generationAffichage());
			}
			return noeud;
		}
		return noeud;
	}
	
	public void complementNoeud(int valeur, Noeud pere)
	{
		this.valeur = valeur;
		this.pere = pere;
	}

	public void generationArbre()
	{
		Vector sousExemle;
		attribut = attributGanant();
		if(attribut!=-1)
		{
			fils = new Noeud[valeurAttributs[attribut].size()];
			for(int i = 0;i<valeurAttributs[attribut].size();i++)
			{
				sousExemle = sousExemple(attribut,i);
				if(sousExemle.size()>0)
				{
					fils[i]= new Noeud(valeurAttributs,sousExemle,nbAttributs,nomAttributs);
					fils[i].complementNoeud(i,this);
					fils[i].generationArbre();
				}
			}
			
			/*
			 * � partir d'ici les fils sont form�s, on regarde s'ils ont la m�me �tique
			 * si oui alors ce noeud peux devenir une feuille
			 */
			int previous = -1;
			for(int i=0;i<fils.length;i++)
			{
				if(fils[i]!=null && previous==-1)	// on recherche la premiere etiquette valide
					previous = fils[i].etiquette;
				if(previous!=-1 && fils[i]!=null && fils[i].etiquette!=previous)	// on verifie si les noeuds inferieurs ont la m�me etiquette
					return;
			}
			if(previous==-1)
				return;
			//si on arrive jusqu'ici c'est que les noeuds inferieur ne servent � rien, donc on les virre
			attribut = nbAttributs-1;
			etiquette=previous;
			fils=null;     
		}
		else	// feuille
		{
			attribut = nbAttributs-1;
			etiquette = ((int[])Exemples.elementAt(0))[attribut];
		}

	}
	/**
	 * Donne un sous ensemble d'exemple pour un attribut et une valeur de cette attribut
	 * @param pattribut
	 * @param pvaleur
	 * @return
	 */
	public Vector sousExemple(int pattribut, int pvaleur)
	{
		Vector sousExemples = new Vector();
		int num = Exemples.size();
		for (int i=0; i< num; i++) {
			int [] exemple = (int[])Exemples.elementAt(i);
			if (exemple[pattribut] == pvaleur) sousExemples.addElement(exemple);
		}
		return sousExemples;
	}
	
	/**
	 * Determine quel attribut doit figurer dans ce noeud
	 * @return
	 */
	public int attributGanant()
	{
		double gain[] = new double[nbAttributs-1];
		int attribut = -1;
		double max = -1;
		Vector attributeValues = new Vector();
		for(int i=0;i<nbAttributs-1;i++)
		{
			attributeValues.removeAllElements();
			for(int j=0;j<Exemples.size();j++)
			{
				attributeValues.addElement(new Integer(((int[])Exemples.elementAt(j))[i]));
			}
			gain[i] = gainAttribut(attributeValues);
		}
		for(int i=0;i<gain.length;i++)
		{
			if(gain[i]>max)
			{
				max = gain[i];
				attribut = i;
			}
		}
		if(max==0) return -1;
		return attribut;
	}

	/**
	 * donne le gain d'un vecteur
	 * @param data
	 * @return
	 */
	public double gainAttribut(Vector data)
	{
		double gain = 0;
		double max = data.size();
		int nbApparitionTotal = 0;
		int i = 0;
		double nbApparition = 0;
		double proba = 0;
		while(nbApparitionTotal<max)
		{
			nbApparition = 0;
			for(int j = 0;j<data.size();j++)
			{
				if(((Integer)data.elementAt(j)).intValue() == i)
					{
						nbApparition++;
						nbApparitionTotal++;
					}
			}
			proba = nbApparition/max;
			gain += -(proba)*Math.log(proba);
			i++;
		}
		return gain;
	}

	public int getAttribut()
	{
		return attribut;
	}

	public Noeud[] getFils()
	{
		return fils;
	}

	public Noeud getPere()
	{
		return pere;
	}

	public int getValeur()
	{
		return valeur;
	}

	
}
