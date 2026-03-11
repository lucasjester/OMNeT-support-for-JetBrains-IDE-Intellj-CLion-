package test.example;

simple MyModu<caret>le {
    gates:
        input in;
        output out;
}

module MyNetwork {
    submodules:
        node: MyModule;
}