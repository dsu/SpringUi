package pl.springui.components;

import java.io.Writer;

/**
 * Komponenty obslugujace pisanie do writera - moze troche zwiekszyc wydajnosc
 * 
 * @author dsu
 *
 */
public interface UiWriterRenderComponent {

	void write(Writer writer);

}
