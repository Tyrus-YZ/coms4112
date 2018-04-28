JFLAGS = -g

JC = javac

J=java

FILE= Algo

default: compile run

compile: 
	$(JC) $(JFLAGS) $(FILE).java 
	
run:
	$(J) $(FILE) query.txt config.txt > output.txt
	$(J) $(FILE) query.txt config.txt

clean: 
	rm -r *.class