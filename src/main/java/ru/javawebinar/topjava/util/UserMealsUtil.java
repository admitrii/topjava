package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        //List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        //mealsTo.forEach(System.out::println);

        List<UserMealWithExcess> mealsTo = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        //System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles
        Map<LocalDate, Boolean> excessMap = new HashMap<>();
        Map<LocalDate, Integer> caloriesMap = new HashMap<>();
        List<UserMeal> filteredList = new LinkedList<>();
        LocalDate mealDate;
        for (UserMeal meal : meals){
            mealDate = meal.getDateTime().toLocalDate();
            caloriesMap.put(mealDate
                    , caloriesMap.get(mealDate)==null?meal.getCalories():caloriesMap.get(mealDate)+meal.getCalories());
            excessMap.put(mealDate, caloriesMap.get(mealDate)>caloriesPerDay);
            if (meal.getDateTime().toLocalTime().isAfter(startTime)
                    && meal.getDateTime().toLocalTime().isBefore(endTime)){
                filteredList.add(meal);
            }
        }
        return transferMealList(filteredList, excessMap);
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        // TODO Implement by streams
        Map<LocalDate, Integer> caloriesMap = meals.stream()
                .collect(
                        groupingBy(userMeal -> userMeal.getDateTime().toLocalDate(), summingInt(UserMeal::getCalories)));

        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                .map(userMeal -> createMealExcess(userMeal, caloriesMap.get(userMeal.getDateTime().toLocalDate())>caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static Map<LocalDate, Integer> excessForDate(List<UserMeal> userMeals, int excessCaloriesPerDay){
        return userMeals.stream()
                .collect(
                        groupingBy(userMeal -> userMeal.getDateTime().toLocalDate()
                                , summingInt(UserMeal::getCalories)));
    }

    private static UserMealWithExcess createMealExcess(UserMeal userMeal, boolean excess){
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
    }



    private static List<UserMealWithExcess> transferMealList (List<UserMeal> meals, Map<LocalDate, Boolean> excessMap){
        List<UserMealWithExcess> excessList = new LinkedList<>();
        for (UserMeal meal : meals){
            excessList.add(new UserMealWithExcess(
                    meal.getDateTime()
                    ,meal.getDescription()
                    ,meal.getCalories()
                    ,excessMap.get(meal.getDateTime().toLocalDate())));
        }
        return excessList;
    }
}
