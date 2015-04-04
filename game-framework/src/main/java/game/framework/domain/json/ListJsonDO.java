package game.framework.domain.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Any class that extends this cannot have their own fields that persist.
 * Only the List<Element> will persist to the database
 *
 * @author brianj
 */

public class ListJsonDO<Element> extends CasJsonDO {

	@JsonProperty("l")
	private List<Element> elements = new ArrayList<>();

	public List<Element> getElements() {
		return elements;
	}

	public void setElements( List<Element> elements ) {
		this.elements = elements;
	}
}