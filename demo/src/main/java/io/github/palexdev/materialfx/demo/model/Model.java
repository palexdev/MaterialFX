/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.demo.model;

import io.github.palexdev.materialfx.font.FontResources;
import io.github.palexdev.materialfx.utils.FXCollectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.IntStream;

import static io.github.palexdev.materialfx.demo.model.Device.State.OFFLINE;
import static io.github.palexdev.materialfx.demo.model.Device.State.ONLINE;
import static io.github.palexdev.materialfx.demo.model.Device.randomID;

public class Model {
	public static final String ipsum =
			"""
					Lorem Ipsum is simply dummy text of the printing and typesetting industry.
					Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.
					It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.
					It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.
					""";
	public static final FontResources[] notificationsIcons;
	public static final String[] randomText;
	public static final ObservableList<String> strings;
	public static final ObservableList<Person> people;
	public static final ObservableList<Device> devices;

	static {
		notificationsIcons = new FontResources[]{
				FontResources.BELL, FontResources.BELL_ALT,
				FontResources.CALENDAR_ALT_DARK, FontResources.CALENDAR_ALT_SEMI_DARK,
				FontResources.CHART_PIE, FontResources.CIRCLE, FontResources.CIRCLE_EMPTY,
				FontResources.EXCLAMATION_CIRCLE, FontResources.EXCLAMATION_TRIANGLE,
				FontResources.GEAR, FontResources.GOOGLE_DRIVE, FontResources.HOME,
				FontResources.INFO_CIRCLE, FontResources.MUSIC,
				FontResources.USER, FontResources.USERS, FontResources.VIDEO,
				FontResources.X_CIRCLE
		};

		randomText = new String[]{
				"""
				In the United States there are about 350 spoken languages. But coding has us beat with over 700 coding languages in use today! Only two countries speak more languages, Papua New Guinea (836) and Indonesia (710).
				Some programming languages, like Java, Python, and HTML, are more common, but others, like Rust and Kotlin, are used in very specific situations.
				The good news for coders? Once you learn the big ones, the more niche languages come easily.
				""",
				"""
				Have you ever encountered a computer bug? How about a real bug in your computer? In 1947 a technician at Harvard had an issue with the performance of their Mark II computer.
				Once they investigated, they discovered that a moth had gotten into a relay – an actual real live bug. In the logbook, it was noted as “First actual case of bug being found."
				While it is oft-repeated that this is where the term “bug" came to refer to errors that impacted the performance of programs, this is not the case.
				The term “bug" was already in fairly widespread use in technical circles in 1947. Thomas Edison used it in 1869 to describe problems in his own inventions.
				""",
				"""
				Coding and STEM fields may seem like it’s built for boys, but the first person to write our modern understanding of a program was Ada Lovelace.
				Being the only legitimate daughter of the poet, Lord Byron, Ada’s mother feared her daughter would suffer the same madness as her father.
				To stave off the madness as long as possible, she dedicated her daughter to studying math and science.
				While working with a peer on a mechanical general-purpose computer known as the Analytical Engine, she recognized that the machine could go way beyond simple and pure calculations, publishing then the first algorithm intended to be carried out by a machine like this one.
				""",
				"""
				The idea of a computer virus was published in the essay “Theory of self-reproducing automata" by John von Neumann in 1949, but the first replicating computer program was not written until 1971.
				The program was not actively malicious software as it caused no damage to data,
				the only effect being a message it output to the teletype reading “I’M THE CREEPER; CATCH ME IF YOU CAN".
				""",
				"""
				You may be fluent in Javascript or C++ but what NASA engineers really need to know is ADA and HAL/S.
				Up through 2005, NASA was still using a computer language from 1973 specifically designed for their needs called HAL/S (or High-order Assembly Language/Shuttle).
				Although HAL/S is designed primarily for programming on-board computers, it is general enough for almost any application and is used widely across NASA’s projects.
				Newer projects, such as the International Space Station, operate on a programming language called ADA, developed in 1980 and accepted as an international standard programming language in 1995.
				""",
				"""
				In 1972, Steve Wozniak and Steve Jobs collaborated on an arcade game, Breakout, for Atari.
				In 2018, Apple Inc became the first US Trillion dollar company. There’s no doubt, there is big money to be had in coding. And by big money, we mean billions.
				The average salary of a data scientist is up to $100,000. Enjoy computer games? Markus Persson, a Swedish programmer, created and launched the computer game Minecraft in 2009.
				By 2014, Microsoft bought it for $2.5 billion.
				""",
				"""
				Computers operate on what is called a “binary code." All of the software that runs them is written using only 0’s and 1’s,
				and there are infinite combinations of these two digits. That’s why new software can be written all the time.
				""",
				"""
				As of the end of 2020, 70% of coding jobs are in career fields not connected with technology.
				Those who learn to code early and well will have a choice of many careers in almost every industry imaginable.
				""",
				"""
				The first-ever computer game made zero profit for its team of creators.
				The game, titled Spacewar, was built from the ground up by Steve Russel, a young computer programmer, and his passionate team of fellow developers.
				Incredibly, Steve and his team chose not to charge people to play Spacewar, and instead, happily shared their creation with anyone who wanted to try it out.
				"""
		};

		strings = IntStream.rangeClosed(1, 25)
				.mapToObj(i -> "String " + i)
				.collect(FXCollectors.toList());

		people = FXCollections.observableArrayList(
				Person.ofSplit("Turner Romero", " ").randomAge(),
				Person.ofSplit("Harley Hays", " ").randomAge(),
				Person.ofSplit("Jeffrey Cannon", " ").randomAge(),
				Person.ofSplit("Simeon Huang", " ").randomAge(),
				Person.ofSplit("Jennifer Donovan", " ").randomAge(),
				Person.ofSplit("Hezekiah Stout", " ").randomAge(),
				Person.ofSplit("Roberto Evans", " ").randomAge(),
				Person.ofSplit("Braxton Watts", " ").randomAge(),
				Person.ofSplit("Jayvon Wilkinson", " ").randomAge(),
				Person.ofSplit("Anabelle Chang", " ").randomAge(),
				Person.ofSplit("Abigayle Christensen", " ").randomAge(),
				Person.ofSplit("Fletcher May", " ").randomAge(),
				Person.ofSplit("Marisol Morris", " ").randomAge(),
				Person.ofSplit("Grant Wilson", " ").randomAge(),
				Person.ofSplit("Hayden Baldwin", " ").randomAge(),
				Person.ofSplit("Markus Davidson", " ").randomAge(),
				Person.ofSplit("Madelyn Farmer", " ").randomAge(),
				Person.ofSplit("Deandre Crosby", " ").randomAge(),
				Person.ofSplit("Casey Hardy", " ").randomAge(),
				Person.ofSplit("Carmelo Velazquez", " ").randomAge(),
				Person.ofSplit("Phillip Hays", " ").randomAge(),
				Person.ofSplit("Damari Mcfarland", " ").randomAge(),
				Person.ofSplit("Selina Norton", " ").randomAge(),
				Person.ofSplit("Lukas Vaughan", " ").randomAge(),
				Person.ofSplit("Charlie Carney", " ").randomAge()
		);

		devices = FXCollections.observableArrayList(
				new Device(randomID(), "HP Gaming Laptop", "144.156.1.1", "Me", ONLINE),
				new Device(randomID(), "Own Gaming Desktop", "", "Me", OFFLINE),
				new Device(randomID(), "Pear fPhone X", "144.156.1.98", "Me", ONLINE),
				new Device(randomID(), "Alexa Echo Dot", "144.156.1.71", "Me", ONLINE),
				new Device(randomID(), "Surface Pro", "", "Wife", OFFLINE),
				new Device(randomID(), "Pear sPhone S", "144.156.1.70", "Wife", ONLINE),
				new Device(randomID(), "Chromebook", "", "Wife", OFFLINE),
				new Device(randomID(), "Wife Gaming Desktop", "144.156.1.69", "Wife", ONLINE),
				new Device(randomID(), "Chromecast", "", "", OFFLINE),
				new Device(randomID(), "Smart Dishwasher", "144.156.1.7", "", ONLINE),
				new Device(randomID(), "Samsung Smart TV", "", "", OFFLINE),
				new Device(randomID(), "Google Home Mini", "144.156.1.58", "", ONLINE),
				new Device(randomID(), "Roomba Killer 2000", "144.156.1.42", "", ONLINE),
				new Device(randomID(), "Smart WC", "144.156.1.99", "", ONLINE),
				new Device(randomID(), "Kids Gaming Desktop", "144.156.1.127", "Kids", ONLINE),
				new Device(randomID(), "Kids Gaming Laptop", "144.156.1.153", "Kids", ONLINE),
				new Device(randomID(), "XBox 360 No Scope Edition", "", "Kids", OFFLINE),
				new Device(randomID(), "PS5", "", "Kids", OFFLINE),
				new Device(randomID(), "PS6", "", "My cousin", OFFLINE),
				new Device(randomID(), "Smart Watch", "144.156.1.155", "Kids", ONLINE),
				new Device(randomID(), "Smart Watch", "144.156.1.154", "Kids", ONLINE),
				new Device(randomID(), "Kids' Smartphone", "144.156.1.221", "Kids", ONLINE),
				new Device(randomID(), "Kids Smartphone", "", "Kids", OFFLINE),
				new Device(randomID(), "Home Lights Controller", "144.156.1.91", "", ONLINE),
				new Device(randomID(), "WiFi Extender", "144.156.1.10", "", ONLINE)
		);
	}
}
