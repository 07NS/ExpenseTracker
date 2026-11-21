package com.expensetracker.utilities;

/**
 * Utility for formatting and parsing currency in Indian Rupees (₹)
 * using the Indian Numbering System (Lakhs and Crores).
 * Example: 1500 -> ₹1,500.00 | 1250000.75 -> ₹12,50,000.75
 */
public class CurrencyFormatter {

    public static String format(double amount) {
        return "₹" + formatIndian(amount, true);
    }

    public static String formatCompact(double amount) {
        return "₹" + formatIndian(amount, false);
    }

    public static String formatIndian(double amount, boolean showDecimals) {
        long intPart = (long) Math.abs(amount);
        long decPart = Math.round((Math.abs(amount) - intPart) * 100);
        if (decPart == 100) {
            intPart++;
            decPart = 0;
        }

        String numStr = String.valueOf(intPart);
        StringBuilder sb = new StringBuilder();

        if (numStr.length() <= 3) {
            sb.append(numStr);
        } else {
            // Last 3 digits (hundreds)
            String lastThree = numStr.substring(numStr.length() - 3);
            String remaining = numStr.substring(0, numStr.length() - 3);

            // Group remaining digits by 2s (Thousands, Lakhs, Crores)
            StringBuilder prefix = new StringBuilder();
            int count = 0;
            for (int i = remaining.length() - 1; i >= 0; i--) {
                if (count == 2) {
                    prefix.insert(0, ",");
                    count = 0;
                }
                prefix.insert(0, remaining.charAt(i));
                count++;
            }
            sb.append(prefix).append(",").append(lastThree);
        }

        if (showDecimals) {
            sb.append(String.format(".%02d", decPart));
        }

        return (amount < 0 ? "-" : "") + sb.toString();
    }

    public static double parseAmount(String input) throws NumberFormatException {
        if (input == null || input.trim().isEmpty()) {
            throw new NumberFormatException("Input amount is empty");
        }
        String clean = input.trim()
                .replace("₹", "")
                .replaceAll("(?i)rs\\.?\\s*", "")
                .replace(",", "")
                .trim();
        return Double.parseDouble(clean);
    }
}
