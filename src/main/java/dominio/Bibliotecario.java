package dominio;

import java.time.LocalDate;
import java.util.Date;

import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;

public class Bibliotecario {

	public static final String EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE = "El libro no se encuentra disponible";
	public static final String EL_LIBRO_ES_PALINDROMO = "Los libros palíndromos solo se pueden utilizar en la biblioteca";

	private RepositorioLibro repositorioLibro;
	private RepositorioPrestamo repositorioPrestamo;

	public Bibliotecario(RepositorioLibro repositorioLibro, RepositorioPrestamo repositorioPrestamo) {
		this.repositorioLibro = repositorioLibro;
		this.repositorioPrestamo = repositorioPrestamo;

	}

	public LocalDate getFechaActual() {
		return LocalDate.now();
	}
	
	public void prestar(String isbn, String nombreUsuario) {
		
		String isbnInvertido = "";
		for(int i=isbn.length()-1; i>=0; i--) {
			isbnInvertido = isbnInvertido + isbn.charAt(i);
		}
		
		String digitosIsbn = isbn.replaceAll("[^0-9]", "");
		int suma = 0;
		for(int i=digitosIsbn.length()-1; i>=0; i--) {
			suma = suma + Character.getNumericValue(digitosIsbn.charAt(i));
		}
		
		LocalDate fechaEntregaMaxima = null;
		if(suma>30) {
			fechaEntregaMaxima = getFechaActual();
			fechaEntregaMaxima = fechaEntregaMaxima.plusDays(15);
			
			if(fechaEntregaMaxima.getDayOfWeek().name().equals("SUNDAY")) {
				fechaEntregaMaxima = fechaEntregaMaxima.plusDays(1);
			}
		}
		
		if(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(isbn)!=null) {
			throw new PrestamoException(EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE);
		}else if(isbn.equals(isbnInvertido)){
			throw new PrestamoException(EL_LIBRO_ES_PALINDROMO);
		}else if(suma>30){
			repositorioPrestamo.agregar(new Prestamo(new Date(), repositorioLibro.obtenerPorIsbn(isbn), java.sql.Date.valueOf(fechaEntregaMaxima), nombreUsuario));
		}else {
			repositorioPrestamo.agregar(new Prestamo(repositorioLibro.obtenerPorIsbn(isbn), nombreUsuario));
		}

	}

	public boolean esPrestado(String isbn) {
		if(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(isbn)!=null) {
			return true;
		}else {
			return false;
		}
	}

}
