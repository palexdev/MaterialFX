package io.github.palexdev.materialfx.demo.model;

import io.github.palexdev.materialfx.utils.RandomUtils;

public class Person {
	private final String name;
	private final String surname;
	private int age;

	public Person(String name, String surname) {
		this.name = name;
		this.surname = surname;
	}

	public Person(String name, String surname, int age) {
		this.name = name;
		this.surname = surname;
		this.age = age;
	}

	public static Person ofSplit(String fullName, String split) {
		String[] fNameArray = fullName.split(split);
		return new Person(fNameArray[0], fNameArray[1]);
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Person randomAge() {
		setAge(RandomUtils.random.nextInt(18, 81));
		return this;
	}
}
