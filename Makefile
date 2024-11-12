run: compile
	java -cp ./build:/usr/share/beast2-mcmc/beast.jar:/usr/share/java/antlr4-runtime.jar Main

compile:
	@echo "Compiling..."
	@javac -cp beast2/src:/usr/share/java/beagle.jar:/usr/share/java/jam.jar -d build tauSpace/*.java
	@echo "Compilation complete."

clean:
	@echo "Cleaning..."
	@rm -rf build/*
	@echo "Clean complete."

test: compile
	java -cp ./build:/usr/share/beast2-mcmc/beast.jar:/usr/share/java/antlr4-runtime.jar Main < test.nex
