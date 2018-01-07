package pl.springui.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringArrayToBolean implements Converter<String[], Boolean> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Boolean convert(String[] source) {
		if (source.length == 1) {
			return isTrue(source[0]);
		} else if (source.length > 0) {
			for (String s : source) {
				if (isTrue(s)) {
					return true;
				}
			}
		}

		return false;
	}

	protected boolean isTrue(String source) {

		if (source == null) {
			return false;
		}

		return source.equals("true") || source.equals("1") || source.equals("on");
	}

}
