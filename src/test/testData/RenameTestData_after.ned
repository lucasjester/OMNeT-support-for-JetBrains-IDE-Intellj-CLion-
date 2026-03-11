package test.example;

simple RenamedModule {
    gates:
        input in;
        output out;
}

module MyNetwork {
    submodules:
        node: RenamedModule;
}