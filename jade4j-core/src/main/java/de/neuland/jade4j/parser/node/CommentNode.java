package de.neuland.jade4j.parser.node;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

public class CommentNode extends Node {
    private boolean buffered;

   	@Override
   	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
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
