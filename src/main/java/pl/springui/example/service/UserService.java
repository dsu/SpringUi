package pl.springui.example.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pl.springui.example.model.User;

@Service("prototype")
public class UserService implements PagedDataService<User> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected List<User> list = new ArrayList<>();

	{
		list.add(new User("Jan", 31));
		list.add(new User("Andrzej", 41));
		list.add(new User("Arek", 40));
		list.add(new User("Staszek", 38));
		list.add(new User("Marian", 12));
		list.add(new User("Wacław", 36));
		list.add(new User("Gerwazy", 5));
		list.add(new User("Darzbór", 13));
		list.add(new User("Zbyś", 23));
		list.add(new User("Władek", 32));
		list.add(new User("Jurek", 55));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.springui.example.service.PagedDataService#getAllElements()
	 */
	@Override
	public List<User> getAllElements() {
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.springui.example.service.PagedDataService#geElements(int, int)
	 */
	@Override
	public List<User> geElements(int page, int limit) {

		if (page < 1) {
			page = 0;
			// warning
		}

		int offset = (page - 1) * limit;
		int allCount = getAllCount();
		int lastIndex = (offset + limit) >= allCount ? allCount : (offset + limit);
		logger.debug("offset  :" + offset + " to " + lastIndex);
		return list.subList(offset, lastIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.springui.example.service.PagedDataService#getAllCount()
	 */
	@Override
	public int getAllCount() {
		logger.debug("allCount {}", list.size());
		return list.size();
	}

}
