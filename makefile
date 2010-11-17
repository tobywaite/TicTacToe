JFLAGS = -d . -sourcepath ./src
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		src/TicTacToe.java \
		 
default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class