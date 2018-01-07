package pl.springui.example.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import pl.springui.components.form.FormInput;
import pl.springui.components.form.InputType;
import pl.springui.components.list.ListColumn;

/**
 * Validation annotations can be assigned either to fields itself or getters
 * 
 * @author dsu
 *
 */
public class User {

	private int userId = 0;

	@NotNull
	@Size(min = 2, max = 30)
	private String name;

	@Min(18)
	@Max(120)
	private Integer age;

	private String birthday;

	public User() {
		super();
	}

	public User(int id, String name, Integer age) {
		super();
		this.name = name;
		this.age = age;
		this.userId = id;
	}

	@NotNull
	@Min(18)
	@ListColumn(name = "AGE", position = 2)
	public Integer getAge() {
		return age;
	}

	public String getBirthday() {
		return birthday;
	}

	@ListColumn(name = "NAME", position = 1)
	public String getName() {
		return this.name;
	}

	@ListColumn(name = "userId", key = true, visible = false)
	public int getUserId() {
		return userId;
	}

	@FormInput(type = InputType.NUMBER)
	public void setAge(Integer age) {
		this.age = age;
	}

	//@FormInput(type = InputType.DATE)
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	@FormInput(type = InputType.TEXT)
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", age=" + age + ", birthday=" + birthday + "]";
	}

}