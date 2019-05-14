package com.eltaieb;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import com.eltaieb.model.Category;
 
public class FileProcessor {

	Map<Integer, Category> categoryIndeciesMap = new HashMap<>();
	Map<String, List<Integer>> keywordCategoriesIndeciesMap = new HashMap<>();
	private int totalNumberOfCategories;
	public FileProcessor() {
		List<Category> categories = loadCategories();
		processCategoriesKeywords(categories);
	}

	private void processCategoriesKeywords(List<Category> categories) {
		totalNumberOfCategories=categories.size();
		for (int i = 0; i < totalNumberOfCategories; i++) {
			// i is the index of current category
			Category category = categories.get(i);
			categoryIndeciesMap.put(i, category);
			for (String keyword : category.getKeywords()) {
				List<Integer> keywordCategories = keywordCategoriesIndeciesMap.get(keyword);
				if (null == keywordCategories) {
					keywordCategories = new ArrayList<>();
					keywordCategoriesIndeciesMap.put(keyword.toUpperCase(), keywordCategories);
				}
				keywordCategories.add(i);
			}
		}

	}

	private List<Category> loadCategories() {
		List<Category> categories = new ArrayList<>();
		Category computersCategory = prepareDummyComputerCategory();
		Category clothingCategory = prepareDummyClothingCategory();
		categories.add(computersCategory);
		categories.add(clothingCategory);
		return categories;

	}

	private Category prepareDummyClothingCategory() {
		Category category = new Category();
		category.setId(UUID.randomUUID().toString());
		category.setCreateDate(new Date());
		category.setTitle("Computers");
		category.setKeywords(Arrays.asList("Networking", " Keyboard", "Mouse", "Processor", "RAM"));
		return category;
	}

	private Category prepareDummyComputerCategory() {
		Category category = new Category();
		category.setId(UUID.randomUUID().toString());
		category.setCreateDate(new Date());
		category.setTitle("Clothing");
		category.setKeywords(Arrays.asList("Pants", "Shoes", "T-Shirt", "Dress Shirt", "Socks"));
		return category;
	}

	private Category processFile(String classpathFilePath) {
		
		int[] categoryIndexAssociationCount = new int[totalNumberOfCategories];
		
		try (Stream<String> stream = Files.lines(Paths.get(ClassLoader.getSystemResource(classpathFilePath).toURI()),StandardCharsets.UTF_8)) {
			stream.forEach(line ->{
				String [] words = line.split("\\s");
				for(String word: words)
				{
					List<Integer>associatedCategoriesIndecies = keywordCategoriesIndeciesMap.get(word.toUpperCase());
					if(null != associatedCategoriesIndecies)
					{
						for(int categoryIndex : associatedCategoriesIndecies)
						{
							categoryIndexAssociationCount[categoryIndex]++;
						}
					}
				}
			});
			
			
			int fileProposedCategoryIndex = -1;
			int tempNumber=0;
			for(int i=0;i<categoryIndexAssociationCount.length; i++)
			{
				if(categoryIndexAssociationCount[i]>tempNumber)
				{
					tempNumber=categoryIndexAssociationCount[i];
					fileProposedCategoryIndex=i;
				}
			}
			
			if(fileProposedCategoryIndex!=-1)
			{
				return categoryIndeciesMap.get(fileProposedCategoryIndex);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static void main(String[] args) {
		FileProcessor fileProcessor = new FileProcessor();
		String file1ToProcessClassPath="./file1ToProcess.txt";
		String file2ToProcessClassPath="./file2ToProcess.txt";

		Category category = fileProcessor.processFile(file1ToProcessClassPath);
		if(null != category)
		{
			System.out.println(String.format("%s is best matching for category %s", file1ToProcessClassPath , category.getTitle()));
		}else
		{
			System.out.println(String.format("%s doesnt match any exist category" , file1ToProcessClassPath));

		}
		
		category = fileProcessor.processFile(file2ToProcessClassPath);;
		if(null != category)
		{
			System.out.println(String.format("%s is best matching for category %s", file2ToProcessClassPath , category.getTitle()));
		}else
		{
			System.out.println(String.format("%s doesnt match any exist category" , file2ToProcessClassPath));

		}
		
		

	}

}
