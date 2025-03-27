package nl.han.ica.icss.parser;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class Fixtures {

    public static AST uncheckedLevel0() {
        Stylesheet stylesheet = new Stylesheet();
		/*
		p {
			background-color: #ffffff;
			width: 500px;
		}
		*/
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("p"))
                .addChild((new Declaration("background-color"))
                        .addChild(new ColorLiteral("#ffffff")))
                .addChild((new Declaration("width"))
                        .addChild(new PixelLiteral("500px")))
        );
		/*
		a {
			color: #ff0000;
		}
		*/
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("a"))
                .addChild((new Declaration("color"))
                        .addChild(new ColorLiteral("#ff0000")))
        );
		/*
		#menu {
			width: 520px;
		}
		*/
        stylesheet.addChild((new Stylerule())
                .addChild(new IdSelector("#menu"))
                .addChild((new Declaration("width"))
                        .addChild(new PixelLiteral("520px")))
        );
		/*
		.menu {
			color: #000000;
		}
		*/
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".menu"))
                .addChild((new Declaration("color"))
                        .addChild(new ColorLiteral("#000000")))
        );

        return new AST(stylesheet);
    }

    public static AST uncheckedLevel1() {
        Stylesheet stylesheet = new Stylesheet();
		/*
			LinkColor := #ff0000;
			ParWidth := 500px;
			AdjustColor := TRUE;
			UseLinkColor := FALSE;
		 */
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("LinkColor"))
                .addChild(new ColorLiteral("#ff0000"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("ParWidth"))
                .addChild(new PixelLiteral("500px"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("AdjustColor"))
                .addChild(new BoolLiteral(true))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("UseLinkColor"))
                .addChild(new BoolLiteral(false))
        );
   	    /*
	        p {
	        background-color: #ffffff;
	        width: ParWidth;
            }
	    */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("p"))
                .addChild((new Declaration("background-color"))
                        .addChild(new ColorLiteral("#ffffff")))
                .addChild((new Declaration("width"))
                        .addChild(new VariableReference("ParWidth")))
        );
        /*
        a {
	        color: LinkColor;
        }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("a"))
                .addChild((new Declaration("color"))
                        .addChild(new VariableReference("LinkColor")))
        );
        /*
            #menu {
	            width: 520px;
            }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new IdSelector("#menu"))
                .addChild((new Declaration("width"))
                        .addChild(new PixelLiteral("520px")))
        );
        /*
            .menu {
	            color: #000000;
            }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".menu"))
                .addChild((new Declaration("color"))
                        .addChild(new ColorLiteral("#000000")))
        );
        return new AST(stylesheet);
    }

    public static AST uncheckedLevel2() {
        Stylesheet stylesheet = new Stylesheet();
		/*
			LinkColor := #ff0000;
			ParWidth := 500px;
			AdjustColor := TRUE;
			UseLinkColor := FALSE;
		 */
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("LinkColor"))
                .addChild(new ColorLiteral("#ff0000"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("ParWidth"))
                .addChild(new PixelLiteral("500px"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("AdjustColor"))
                .addChild(new BoolLiteral(true))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("UseLinkColor"))
                .addChild(new BoolLiteral(false))
        );
   	    /*
	        p {
	        background-color: #ffffff;
	        width: ParWidth;
            }
	    */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("p"))
                .addChild((new Declaration("background-color"))
                        .addChild(new ColorLiteral("#ffffff")))
                .addChild((new Declaration("width"))
                        .addChild(new VariableReference("ParWidth")))
        );
        /*
        a {
	        color: LinkColor;
        }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("a"))
                .addChild((new Declaration("color"))
                        .addChild(new VariableReference("LinkColor")))
        );
        /*
            #menu {
        	width: ParWidth + 2 * 10px;
            }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new IdSelector("#menu"))
                .addChild((new Declaration("width"))
                        .addChild((new AddOperation())
                                .addChild(new VariableReference("ParWidth"))
                                .addChild((new MultiplyOperation())
                                        .addChild(new ScalarLiteral("2"))
                                        .addChild(new PixelLiteral("10px"))

                                ))));
        /*
            .menu {
	            color: #000000;
            }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".menu"))
                .addChild((new Declaration("color"))
                        .addChild(new ColorLiteral("#000000")))
        );
        return new AST(stylesheet);
    }

    public static AST uncheckedLevel3() {
        Stylesheet stylesheet = new Stylesheet();
		/*
			LinkColor := #ff0000;
			ParWidth := 500px;
			AdjustColor := TRUE;
			UseLinkColor := FALSE;
		 */
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("LinkColor"))
                .addChild(new ColorLiteral("#ff0000"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("ParWidth"))
                .addChild(new PixelLiteral("500px"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("AdjustColor"))
                .addChild(new BoolLiteral(true))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("UseLinkColor"))
                .addChild(new BoolLiteral(false))
        );
   	    /*
	        p {
				background-color: #ffffff;
				width: ParWidth;
				if[AdjustColor] {
	    			color: #124532;
	    			if[UseLinkColor]{
	        			bg-color: LinkColor;
	    			}
				}
			}
			p {
				background-color: #ffffff;
				width: ParWidth;
				if[AdjustColor] {
	    			color: #124532;
	    		if[UseLinkColor]{
	        		background-color: LinkColor;
	    		} else {
	        		background-color: #000000;
	    		}
	    		height: 20px;
			}
}
	    */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("p"))
                .addChild((new Declaration("background-color"))
                        .addChild(new ColorLiteral("#ffffff")))
                .addChild((new Declaration("width"))
                        .addChild(new VariableReference("ParWidth")))
                .addChild((new IfClause())
                        .addChild(new VariableReference("AdjustColor"))
                        .addChild((new Declaration("color")
                                .addChild(new ColorLiteral("#124532"))))
                        .addChild((new IfClause())
                                .addChild(new VariableReference("UseLinkColor"))
                                .addChild(new Declaration("background-color").addChild(new VariableReference("LinkColor")))
                                .addChild((new ElseClause())
                                        .addChild(new Declaration("background-color").addChild(new ColorLiteral("#000000")))

                                )
                        ))
                .addChild((new Declaration("height"))
                        .addChild(new PixelLiteral("20px")))
        );
        /*
        a {
	        color: LinkColor;
        }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("a"))
                .addChild((new Declaration("color"))
                        .addChild(new VariableReference("LinkColor"))
                )
        );
        /*
            #menu {
        	width: ParWidth + 20px;
            }
        */
        stylesheet.addChild((new Stylerule())
                .addChild(new IdSelector("#menu"))
                .addChild((new Declaration("width"))
                        .addChild((new AddOperation())
                                .addChild(new VariableReference("ParWidth"))
                                .addChild(new PixelLiteral("20px"))
                        )
                )
        );
        /*


         .menu {
				color: #000000;
    			background-color: LinkColor;

			}

        */
        stylesheet.addChild((new Stylerule())
                .addChild(new ClassSelector(".menu"))

                .addChild((new Declaration("color"))
                        .addChild(new ColorLiteral("#000000"))
                )
                .addChild((new Declaration("background-color"))
                        .addChild(new VariableReference("LinkColor"))
                )

        );

        return new AST(stylesheet);
    }

    public static AST uncheckedLevel4() {
        Stylesheet stylesheet = new Stylesheet();
		/*
		LinkColor := #ff0000;
		ParWidth := 500px;
		AdjustColor := TRUE;
		UseLinkColor := FALSE;
		FixedMenuHeight := FALSE;
		*/

        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("LinkColor"))
                .addChild(new ColorLiteral("#ff0000"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("ParWidth"))
                .addChild(new PixelLiteral("500px"))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("AdjustColor"))
                .addChild(new BoolLiteral(true))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("UseLinkColor"))
                .addChild(new BoolLiteral(false))
        );
        stylesheet.addChild((new VariableAssignment())
                .addChild(new VariableReference("FixedMenuHeight"))
                .addChild(new BoolLiteral(false))
        );

		/*
		p {
			background-color: #ffffff;
			width: ParWidth;
			if[AdjustColor] {
				color: #124532;
				if[UseLinkColor]{
					background-color: LinkColor;
				} else {
					background-color: #000000;
				}
			}
			height: 20px;
		}
		*/

        stylesheet.addChild((new Stylerule())
                .addChild(new TagSelector("p"))
                .addChild((new Declaration("background-color"))
                        .addChild(new ColorLiteral("#ffffff")))
                .addChild((new Declaration("width"))
                        .addChild(new VariableReference("ParWidth")))
                .addChild((new IfClause())
                        .addChild(new VariableReference("AdjustColor"))
                        .addChild((new Declaration("color")
                                .addChild(new ColorLiteral("#124532"))))
                        .addChild((new IfClause())
                                .addChild(new VariableReference("UseLinkColor"))
                                .addChild(new Declaration("background-color")
                                        .addChild(new VariableReference("LinkColor")))
                                .addChild((new ElseClause())
                                        .addChild(new Declaration("background-color")
                                                .addChild(new ColorLiteral("#000000")))
                                )
                        ))
                .addChild((new Declaration("height"))
                        .addChild(new PixelLiteral("20px")))
        );

		/*
		a {
			color: LinkColor;
		}
		*/

        stylesheet.addChild(new Stylerule()
                .addChild(new TagSelector("a"))
                .addChild(new Declaration("color")
                        .addChild(new VariableReference("LinkColor"))
                )
        );

		/*
		#menu {
			width: ParWidth + 20px;
			if [FixedMenuHeight] {
				height: 100px;
			}
			if [!TRUE || 7 != 3 + 2 ^ (3 - 1)] {
				background-color: #AAAAAA;
			} else if [!(FALSE || 3! * 1px == 10px - 4px)] {
				background-color: #BBBBBB;
			} else if [!(1 > 2) && 4! >= 3 + 4] {
				background-color: #CCCCCC;
			} else {
				background-color: #DDDDDD;
			}
		}
		*/

        stylesheet.addChild(new Stylerule()
                .addChild(new IdSelector("#menu"))
                .addChild(new Declaration("width")
                        .addChild(new AddOperation()
                                .addChild(new VariableReference("ParWidth"))
                                .addChild(new PixelLiteral("20px"))
                        )
                )
                .addChild(new IfClause()
                        .addChild(new VariableReference("FixedMenuHeight"))
                        .addChild(new Declaration("height")
                                .addChild(new PixelLiteral("100px"))
                        )
                )
                .addChild(new IfClause()
                        .addChild(new OrOperation()
                                .addChild(new NotOperation()
                                        .addChild(new BoolLiteral("TRUE"))
                                )
                                .addChild(new NotOperation()
                                        .addChild(new EqualsOperation()
                                                .addChild(new ScalarLiteral("7"))
                                                .addChild(new AddOperation()
                                                        .addChild(new ScalarLiteral("3"))
                                                        .addChild(new PowerOperation()
                                                                .addChild(new ScalarLiteral("2"))
                                                                .addChild(new SubtractOperation()
                                                                        .addChild(new ScalarLiteral("3"))
                                                                        .addChild(new ScalarLiteral("1"))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .addChild(new Declaration("background-color")
                                .addChild(new ColorLiteral("#aaaaaa"))
                        )
                        .addChild(new ElseClause()
                                .addChild(new IfClause()
                                        .addChild(new NotOperation()
                                                .addChild(new OrOperation()
                                                        .addChild(new BoolLiteral("FALSE"))
                                                        .addChild(new EqualsOperation()
                                                                .addChild(new MultiplyOperation()
                                                                        .addChild(new FactorialOperation()
                                                                                .addChild(new ScalarLiteral("3"))
                                                                        )
                                                                        .addChild(new PixelLiteral("1px"))
                                                                )
                                                                .addChild(new SubtractOperation()
                                                                        .addChild(new PixelLiteral("10px"))
                                                                        .addChild(new PixelLiteral("4px"))
                                                                )
                                                        )
                                                )
                                        )
                                        .addChild(new Declaration("background-color")
                                                .addChild(new ColorLiteral("#bbbbbb"))
                                        )
                                        .addChild(new ElseClause()
                                                .addChild(new IfClause()
                                                        .addChild(new AndOperation()
                                                                .addChild(new NotOperation()
                                                                        .addChild(new GreaterThanOperation()
                                                                                .addChild(new ScalarLiteral("1"))
                                                                                .addChild(new ScalarLiteral("2"))
                                                                        )
                                                                )
                                                                .addChild(new NotOperation()
                                                                        .addChild(new GreaterThanOperation()
                                                                                .addChild(new AddOperation()
                                                                                        .addChild(new ScalarLiteral("3"))
                                                                                        .addChild(new ScalarLiteral("4"))
                                                                                )
                                                                                .addChild(new FactorialOperation()
                                                                                        .addChild(new ScalarLiteral("4"))
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                        .addChild(new Declaration("background-color")
                                                                .addChild(new ColorLiteral("#cccccc"))
                                                        )
                                                        .addChild(new ElseClause()
                                                                .addChild(new Declaration("background-color")
                                                                        .addChild(new ColorLiteral("#dddddd"))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

		/*
		.menu {
			color: #000000;
			background-color: LinkColor;

		}
		 */
        stylesheet.addChild(new Stylerule()
                .addChild(new ClassSelector(".menu"))
                .addChild(new Declaration("color")
                        .addChild(new ColorLiteral("#000000"))
                )
                .addChild(new Declaration("background-color")
                        .addChild(new VariableReference("LinkColor"))
                )
        );

        return new AST(stylesheet);
    }
}
