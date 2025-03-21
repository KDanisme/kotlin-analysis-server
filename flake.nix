{
  outputs =
    inputs:
    inputs.flake-parts.lib.mkFlake { inherit inputs; } {
      systems = [ "x86_64-linux" ];

      imports = [
        inputs.flake-root.flakeModule
        inputs.treefmt-nix.flakeModule
        inputs.git-hooks.flakeModule
      ];
      perSystem =
        {
          pkgs,
          config,
          ...
        }:
        {
          # _module.args.pkgs = import inputs.nixpkgs {
          #   inherit system;
          #   config.allowUnfree = true;
          # };

          pre-commit.settings.hooks.treefmt.enable = true;
          treefmt.config = {
            inherit (config.flake-root) projectRootFile;
            flakeCheck = false;
            programs = {
              nixfmt.enable = true;
              shellcheck.enable = true;
            };
          };
          devShells.default = pkgs.mkShell {
            packages = [ pkgs.graalvm-ce ];
            inputsFrom = [
              config.pre-commit.devShell
              config.treefmt.build.devShell
            ];
            shellHook = ''
              # Configure gradle to use nix gradle
              export _JAVA_OPTIONS="$_JAVA_OPTIONS -Didea.gradle.distributionType=LOCAL -Dorg.gradle.java.home=${pkgs.gradle.jdk} -Didea.gradle.home=${pkgs.gradle}/lib/gradle"
            '';
          };
        };
    };
  inputs = {
    nixpkgs.url = "nixpkgs/nixpkgs-unstable";
    flake-parts.url = "github:hercules-ci/flake-parts";

    flake-root.url = "github:srid/flake-root";
    git-hooks = {
      url = "github:cachix/git-hooks.nix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
    treefmt-nix = {
      url = "github:numtide/treefmt-nix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };
}
