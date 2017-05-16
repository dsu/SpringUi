package pl.springui.example.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import pl.springui.components.list.ListColumn;

/**
 * Validation annotations can be assigned either to fields itself or getters
 * 
 * @author dsu
 *
 */
public class User {

	@NotNull
	@Size(min = 2, max = 30)
	private String name;
	private Integer age;

	public User(String name, Integer age) {
		super();
		this.name = name;
		this.age = age;
	}

	public User() {
		super();
	}

	@ListColumn(name = "IMIĘ", position = 1)
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@NotNull
	@Min(18)
	@ListColumn(name = "WIEK", position = 2)
	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String toString() {
		return "Person(Name: " + this.name + ", Age: " + this.age + ")";
	}
}