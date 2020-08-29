package gillberg.holst.enums;

public enum Feature {
			buse_readability,
			scalabrino_readability,
			cyclomatic_complexity,
			num_stops,
			num_parens_brackets,
			avg_line_length,
			max_line_length,
			num_identifiers,
			num_if_statements,
			num_loops,
			num_catch,
			num_and_or;

	public String toString() {
		return name();
	}

}
