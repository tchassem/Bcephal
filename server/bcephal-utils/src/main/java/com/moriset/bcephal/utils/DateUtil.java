package com.moriset.bcephal.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
	private static final String FORMAT_PATTERN = "yyyy/MM/dd";// "yyyy/MM/dd HH:mm:ss"
	public static final DateFormat FORMATER = new SimpleDateFormat(FORMAT_PATTERN, Locale.US);

	private Date format(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date == null) {
			return null;
		}
		cal.setTime(date);
		try {
			return FORMATER.parse(FORMATER.format(new Date(cal.getTimeInMillis())));
		} catch (ParseException e) {
		}
		return cal.getTime();
	}

	public Date getPlusOneMonth(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		}
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		cal.add(Calendar.MONTH, 1);		
		return format(new Date(cal.getTimeInMillis()));
	}
	
	public Date getFirstDayOfWeek(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		}
		// get today and clear time of day
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		// get start of this week in milliseconds
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		return format(new Date(cal.getTimeInMillis()));
	}

	public Date getLastDayOfWeek(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		}
		// get today and clear time of day
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		// get start of this week in milliseconds
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		cal.add(Calendar.DATE, 6);
		return format(new Date(cal.getTimeInMillis()));
	}

	public Date getFirstDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		}
		// get today and clear time of day
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		// get start of the month
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return format(new Date(cal.getTimeInMillis()));
	}

	public Date getLastDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		}
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		// get start of the month
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		return format(new Date(cal.getTimeInMillis()));
	}

	public Date getFirstDayOfYear(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		}
		// get today and clear time of day
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		// get start of the month
		cal.set(Calendar.DAY_OF_YEAR, 1);
		return format(new Date(cal.getTimeInMillis()));
	}

	public Date getLastDayOfYear(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		}
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		// get start of the month
		cal.set(Calendar.DAY_OF_YEAR, 1);
		cal.add(Calendar.YEAR, 1);
		cal.add(Calendar.DATE, -1);
		return format(new Date(cal.getTimeInMillis()));
	}

	public Date getFirstDayOfQuarter(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		}
		// get today and clear time of day
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		// get start of the month
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3);
		return format(new Date(cal.getTimeInMillis()));
	}

	public Date getLastDayOfQuarter(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		}
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		// get start of the month
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3 + 2);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return format(new Date(cal.getTimeInMillis()));
	}

	public Date getCurrentDay() {
		Calendar cal = Calendar.getInstance(Locale.US);
		return format(new Date(cal.getTimeInMillis()));
	}

	public Date[] getCurrentWeek() {
		return new Date[] { getFirstDayOfWeek(null), getLastDayOfWeek(null) };
	}

	public Date[] getCurrentMonth() {
		return new Date[] { getFirstDayOfMonth(null), getLastDayOfMonth(null) };
	}

	public Date[] getCurrentYear() {
		return new Date[] { getFirstDayOfYear(null), getLastDayOfYear(null) };
	}

	public Date[] getCurrentQuarter() {
		return new Date[] { getFirstDayOfQuarter(null), getLastDayOfQuarter(null) };
	}

	public Date[] getPreviewWeek(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		}
		// get today and clear time of day
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		// get start of this week in milliseconds
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		cal.add(Calendar.DATE, -1);
		return new Date[] { getFirstDayOfWeek(cal.getTime()), getLastDayOfWeek(cal.getTime()) };
	}

	public Date[] getPreviewWeek() {
		return getPreviewWeek(null);
	}

	public Date[] getNextWeek(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		}
		// get today and clear time of day
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		// get start of this week in milliseconds
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		cal.add(Calendar.DATE, 7);
		return new Date[] { getFirstDayOfWeek(cal.getTime()), getLastDayOfWeek(cal.getTime()) };
	}

	public Date[] getNextWeek() {
		return getNextWeek(null);
	}

	public Date[] getPreviewMonth(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		}
		// get today and clear time of day
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		// get start of this week in milliseconds
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DATE, -1);
		return new Date[] { getFirstDayOfMonth(cal.getTime()), getLastDayOfMonth(cal.getTime()) };
	}

	public Date[] getPreviewMonth() {
		return getPreviewMonth(null);
	}

	public Date[] getNextMonth(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		}
		// get today and clear time of day
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		// get start of this week in milliseconds
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, 1);
		return new Date[] { getFirstDayOfMonth(cal.getTime()), getLastDayOfMonth(cal.getTime()) };
	}

	public Date[] getNextMonth() {
		return getNextMonth(null);
	}

	public Date[] getPreviewYear(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		}
		// get today and clear time of day
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		// get start of this week in milliseconds
		cal.set(Calendar.DAY_OF_YEAR, 1);
		cal.add(Calendar.DATE, -1);
		return new Date[] { getFirstDayOfYear(cal.getTime()), getLastDayOfYear(cal.getTime()) };
	}

	public Date[] getPreviewYear() {
		return getPreviewYear(null);
	}

	public Date[] getNextYear(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		}
		// get today and clear time of day
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		// get start of this week in milliseconds
		cal.set(Calendar.DAY_OF_YEAR, 1);
		cal.add(Calendar.YEAR, 1);
		return new Date[] { getFirstDayOfYear(cal.getTime()), getLastDayOfYear(cal.getTime()) };
	}

	public Date[] getNextYear() {
		return getNextYear(null);
	}

	public Date[] getPreviewQuarter(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		} else {
			return getPreviewQuarter();
		}
		// get today and clear time of day
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		// get start of the month
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3);
		cal.add(Calendar.DATE, -1);
		return new Date[] { getFirstDayOfQuarter(cal.getTime()), getLastDayOfQuarter(cal.getTime()) };
	}

	public Date[] getPreviewQuarter() {
		return getPreviewQuarter(null);
	}

	public Date[] getNextQuarter(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		if (date != null) {
			cal.clear();
			cal.setTime(date);
		} else {
			return getNextQuarter();
		}
		// get today and clear time of day
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		// get start of the month
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3 + 2);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.add(Calendar.DATE, 1);
		return new Date[] { getFirstDayOfQuarter(cal.getTime()), getLastDayOfQuarter(cal.getTime()) };
	}

	public Date[] getNextQuarter() {
		return getNextQuarter(null);
	}

	public boolean intoInterval(Date inDate, Date[] intervalDate) {
		if (intervalDate == null || intervalDate.length != 2 || intervalDate[0] == null || intervalDate[1] == null || inDate == null) {
			return false;
		}
		
		inDate = format(inDate);
		intervalDate[0] = format(intervalDate[0]);
		intervalDate[1] = format(intervalDate[1]);
		
		if(inDate.after(intervalDate[0]) && inDate.before(intervalDate[1])) {
			return true;
		}
		
		if(inDate.compareTo(intervalDate[0]) == 0 || inDate.compareTo(intervalDate[1]) == 0) {
			return true;
		}

		return false;
	}
}
