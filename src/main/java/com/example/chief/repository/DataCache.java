package com.example.chief.repository;

import java.util.HashMap;

import com.example.chief.model.*;

public class DataCache {
	public static HashMap<Integer, Tests> test_map = new HashMap<>();
	public static HashMap<Integer, Submissions> sub_map = new HashMap<>();
	public static HashMap<Integer, Questions> ques_map = new HashMap<>();
}
