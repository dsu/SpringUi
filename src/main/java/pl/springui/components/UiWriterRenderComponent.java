package pl.springui.components;

import java.io.Writer;
import java.util.Collection;

/**
 * Komponenty obslugujace pisanie do writera - moze troche zwiekszyc wydajnosc
 * 
 * @author dsu
 *
 */
public interface UiWriterRenderComponent {

	void write(Writer writer);

}
