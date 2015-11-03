package de.neuland.jade4j.parser.node;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MixinNode extends CallNode {
	@Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
		if (isCall()) {
			super.execute(writer, model, template);
		} else {
			model.setMixin(getName(), this);


   			List<String> args = getArguments();

//			String rest;
//
//   			//Überprüfe ob letztes Argument alles restlichen Argumente enhalten soll.
//			if (args.size()>0 ) {
//				Matcher matcher = Pattern.compile("^\\.\\.\\.").matcher(args.get(args.size()-1).trim());
//				if(matcher.find(0))
//     				rest = args.remove(args.size()-1).trim().replaceAll("^\\.\\.\\.", "");
//   			}

		}
	}

}
