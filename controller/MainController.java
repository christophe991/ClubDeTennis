package org.pland.bt.controller;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ResourceBundle;

import org.pland.bt.Joueurs;

public class MainController implements Initializable {

	@FXML
	private Label Labelinfos;

	@FXML
	private TextField PrenomField;

	@FXML
	private TextField NomField;

	@FXML
	private TextField NiveauField;

	@FXML
	private TextField AgeField;

	@FXML
	private Button insertButton;

	@FXML
	private Button updateButton;

	@FXML
	private Button deleteButton;

	@FXML
	private TableView<Joueurs> TableView;

	@FXML
	private TableColumn<Joueurs, Integer> idColumn;

	@FXML
	private TableColumn<Joueurs, String> prenomColumn;

	@FXML
	private TableColumn<Joueurs, String> nomColumn;

	@FXML
	private TableColumn<Joueurs, String> niveauColumn;

	@FXML
	private TableColumn<Joueurs, Integer> ageColumn;

	Connection connexion;

	private ObservableList<Joueurs> booksList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {// Cette m�thode se charge automatiquement. Donc
																	// c'est la premi�re � se charger sur cette page

		LoadDatabse();
		idColumn.setCellValueFactory(new PropertyValueFactory<Joueurs, Integer>("id"));// cr�� les cellules de la
																						// colonne
		nomColumn.setCellValueFactory(new PropertyValueFactory<Joueurs, String>("nom"));
		prenomColumn.setCellValueFactory(new PropertyValueFactory<Joueurs, String>("prenom"));
		niveauColumn.setCellValueFactory(new PropertyValueFactory<Joueurs, String>("niveau"));
		ageColumn.setCellValueFactory(new PropertyValueFactory<Joueurs, Integer>("age"));

		showPersonDetails(null);

		// le listener renvoie les changements sur les textfield en fonction de la
		// s�lection sur la table
		TableView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showPersonDetails(newValue));

	}

	@FXML
	private void insertButton() {

		String query = "INSERT INTO joueurs (nom, prenom, niveau, age) values('" + NomField.getText() + "','"
				+ PrenomField.getText() + "','" + NiveauField.getText() + "'," + AgeField.getText() + ")";

		if (AgeField.getText().isEmpty()) {// message d'alerte si le champs AgeField est vide
			Alert alert = new Alert(AlertType.WARNING);

			alert.setTitle("Erreur");
			alert.setHeaderText("Certains champs sont vides !");
			alert.setContentText("remplissez tous les champs s'il vous pla�t !");

			alert.showAndWait();
		}

		else {

			executeQuery2(query);

			LoadDatabse();// mise � jour de la table

			Labelinfos.setText("insertion effectu�e");// montre un message de confirmation dans la label
			Fading(Labelinfos); // le texte disparait gr�ce � la methode fadingg

		}
	}

	@FXML
	private void updateButton() {
		try {
			Joueurs book2 = (Joueurs) TableView.getSelectionModel().getSelectedItem();// retourne l'objet qui se trouve
																						// � l'endroit s�lectionn�
																						// dans ce cas il s'agit de
																						// l'objet book2

			if (book2 == null) {// alert message
				Alert alert = new Alert(AlertType.WARNING);

				alert.setTitle("Erreur");
				alert.setHeaderText("Aucune personne s�lectionn�e");
				alert.setContentText("S�lectionner une personne");

				alert.showAndWait();
			}

			else {

				String query = "UPDATE joueurs SET nom=?,prenom=? ,niveau=?,age=? WHERE ID=?";
				Connection connexion = getConnection();
				PreparedStatement st = connexion.prepareStatement(query);

				st.setString(1, NomField.getText());
				st.setString(2, PrenomField.getText());
				st.setString(3, NiveauField.getText());
				st.setString(4, AgeField.getText());
				st.setInt(5, book2.getId());// on prend l'id de l'objet book2 et pas d'un textfield
				st.executeUpdate();

				st.close();

				Labelinfos.setText("modification effectu�e");// montre un message de confirmation dans la label
				Fading(Labelinfos); // le texte disparait gr�ce � la methode fadingg

			}

		} catch (SQLException e) {
			System.out.println(e);

		}
		LoadDatabse();
	}

	@FXML
	private void deleteButton() {

		try {
			Joueurs book3 = (Joueurs) TableView.getSelectionModel().getSelectedItem();

			if (book3 == null) {// alert message
				Alert alert = new Alert(AlertType.WARNING);

				alert.setTitle("Erreur");
				alert.setHeaderText("Aucune personne s�lectionn�e");
				alert.setContentText("S�lectionner une personne");

				alert.showAndWait();
			}

			else {

				String query = "DELETE FROM joueurs WHERE id=?";
				Connection connexion = getConnection();
				PreparedStatement st = connexion.prepareStatement(query);
				st.setInt(1, book3.getId());// on prend l'id de l'objet book2 et pas d'un textfield
				st.executeUpdate();

				st.close();

				Labelinfos.setText("suppression effectu�e");
				Fading(Labelinfos);

			}

		} catch (SQLException e) {
			System.out.println(e);

		}
		LoadDatabse();

	}

	public void executeQuery2(String query) {
		Connection conn = getConnection();
		Statement st;
		try {
			st = conn.createStatement();
			st.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		Connection conn;
		try {
			String url = "jdbc:mysql://localhost:3306/elevage?serverTimezone=UTC";
			String user = "root";
			String password = "1234";

			// create a connection to the database
			conn = DriverManager.getConnection(url, user, password);
			// more processing here
			// ...
			System.out.println("Connexion effective !");

			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void LoadDatabse() {// m�thode pour mettre � jour la table

		booksList = FXCollections.observableArrayList();// la liste doit �tre dans cette m�thode pour rafraichir la
														// table
														// quand on clique sur un bouton qui va lancer la m�thode
														// LoadDatase
		Connection connection = getConnection();
		String query = "SELECT * FROM joueurs ";
		Statement st;
		ResultSet rs;

		try {
			st = connection.createStatement();
			rs = st.executeQuery(query);
			Joueurs books;
			while (rs.next()) {
				books = new Joueurs(rs.getInt("Id"), rs.getString("Nom"), rs.getString("Prenom"),
						rs.getString("Niveau"), rs.getInt("Age"));
				booksList.add(books);

				TableView.setItems(booksList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void Fading(Label name) {// m�thode pour afficher un message de confirmation dans une label

		FadeTransition fade = new FadeTransition();
		fade.setDuration(Duration.millis(4000));// temps de transition
		fade.setFromValue(10);
		fade.setToValue(0.0);

		fade.setNode(name);
		fade.play();

	}

	private void showPersonDetails(Joueurs books) {// Cette m�thode est pour les textfield
		if (books != null) {
			// remplis les labels avec les informations des joueurs obtenu via le listener
			// dans la m�thode intialize

			NomField.setText(books.getNom());
			PrenomField.setText(books.getPrenom());
			NiveauField.setText(books.getNiveau());
			AgeField.setText(Integer.toString(books.getAge()));

		} else {
			// l'objet joueur est null, on enl�ve le texte
			NomField.setText("");
			PrenomField.setText("");
			NiveauField.setText("");
			AgeField.setText("");
		}
	}

}
