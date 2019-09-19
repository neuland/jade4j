package de.neuland.pug4j.parser.node;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.template.PugTemplate;

public class CommentNode extends Node {
    private boolean buffered;

   	@Override
   	public void execute(IndentWriter writer, PugModel model, PugTemplate template) throws PugCompilerException {
        if (!isBuffered()) {
      			return;
      		}
      	if(writer.isPp()) {
            writer.prettyIndent(1, true);
        }
   		writer.append("<!--");
   		writer.append(value);
   		writer.append("-->");
   	}

   	public boolean isBuffered() {
   		return buffered;
   	}

   	public void setBuffered(boolean buffered) {
   		this.buffered = buffered;
   	}

}
