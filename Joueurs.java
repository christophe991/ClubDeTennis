package org.pland.bt;



public class Joueurs {

	private int id;
    private String prenom;
    private String nom;
    private String niveau;
    private int age;
    
    public Joueurs () {
    	
    }

    public Joueurs (int Id, String Nom, String Prenom, String Niveau, int Age){
        this.id = Id;
    	this.prenom=Prenom;
        this.nom = Nom;
        this.niveau=Niveau;
        this.age=Age;
    }

    public int getId() {
    	return id;
    }
    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }

    public String getNiveau() {
        return niveau;
    }

    public int getAge() {
        return age;
    }
}
