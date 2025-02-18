package br.com.mateusneubarth.todolist.utils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class Utils {

    public static void copyNonNullProperties(Object source, Object target) {
        // Copy the property values of the given source bean into the given target bean, ignoring the given "ignoreProperties".
        // Note: The source and target classes do not have to match or even be derived from each other, 
        // as long as the properties match. 
        // Any bean properties that the source bean exposes but the target bean does not will silently be ignored.
        // This will copy every non null properties of the source object
        // The third parameter makes it so the copyProperties() can ignore every null property in the source object
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    public static String[] getNullPropertyNames(Object source) {
        // Wrap source object with a BeanWrapper
        final BeanWrapper src = new BeanWrapperImpl(source);

        // Get property descriptors
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        // Create the hash set for empty/null properties
        Set<String> emptyNames = new HashSet<>();

        // For every property descriptor, get it's value by name, check if it's null, and add to the empty properties HashSet
        for (PropertyDescriptor pd : pds) {
            // Get property value by name
            Object srcValue = src.getPropertyValue(pd.getName());
            // Check if it's null
            if (srcValue == null) {
                // Save it to the null properties HashSet
                emptyNames.add(pd.getName());
            }
        }

        // Create and return empty properties names as a String array
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
