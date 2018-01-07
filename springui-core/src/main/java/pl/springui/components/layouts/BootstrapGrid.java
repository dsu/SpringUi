package pl.springui.components.layouts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.UiComponent;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

@Component
@Scope("prototype")
public class BootstrapGrid extends UiComponent {

  protected MapTemplateEngine engine;
  protected List<List<UiComponent>> grid = new ArrayList<>();

  @Autowired
  public BootstrapGrid(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
    super(ctx);
    this.engine = engine;
  }

  public void add(UiComponent comp) {
    add(comp, 0);
  }

  public void add(UiComponent comp, int rowNr) {

    while (grid.size() <= rowNr) {
      grid.add(new ArrayList<>());
    }

    List<UiComponent> row = grid.get(rowNr);
    if (row == null) {
      row = new ArrayList<>();
      grid.add(rowNr, row);
    }

    logger.trace("Add item to row : {} as {}", rowNr, grid.get(rowNr).size());

    row.add(comp);
    addChild(comp);
  }

  public void add(UiComponent comp, int rowNr, int colNr) {

    // TODO - warring when overriding

    // add rows
    while (grid.size() < rowNr + 1) {
      grid.add(new ArrayList<>());
    }

    // initialize columns
    List<UiComponent> row = grid.get(rowNr);

    // resize row columns
    if (row.size() < colNr + 1) {
      while (row.size() < colNr + 1) {
        row.add(null);
      }
    }

    logger.trace("Add item to row : {} as {}", rowNr, grid.get(rowNr).size());

    if (row.get(colNr) != null) {
      logger.warn("Overriding a component {} position with {}", row.get(colNr), comp);
      add(comp, rowNr, colNr + 1);
    } else {
      row.set(colNr, comp);
      addChild(comp);
    }
  }

  @Override
  public void clearChildren() {
    grid = new ArrayList<>();
    super.clearChildren();
  }

  public List<List<UiComponent>> getGrid() {
    return grid;
  }

  public int getRows() {
    return grid.size();
  }

  @Profiler
  @Override
  public String renderResponse() {

    if (!ctx.existsInTheTree(getClientId())) {
      logger.warn("Component is not in the tree {}", this);
    }

    putToViewModel("grid", grid);
    // UserList<UserList<String>> renderedGrid = new ArrayList<>(); //ew.
    // zamian
    // tutaj wszystkiego
    return engine.procesTemplateAsString(viewModel, "components/bootstrap-grid.xhtml");
  }

  public void setGrid(List<List<UiComponent>> grid) {
    this.grid = grid;
  }

}
