package com.example.chief.repository;

import java.util.HashMap;
import java.util.HashSet;

import com.example.chief.model.*;

public class DataCache {
	public static HashMap<Integer, Tests> test_map = new HashMap<>();
	public static HashMap<Integer, Submissions> sub_map = new HashMap<>();
	public static HashMap<Integer, Questions> ques_map = new HashMap<>();
	public static HashMap<Integer, Contests> contest_map = new HashMap<>();
	public static HashMap<Integer, Users> user_map = new HashMap<>();
	public static HashMap<Integer, Integer> queue = new HashMap<>();
}
