Directory with projects to handle depdencies from third parties.

The projects in this directory are "virtual": there is no code here, just
"linked ressources" to source repositories.

In order to have this project functional, one needs (see develop.org for
more info):
1) Clone the repo for
   - ioio       https://github.com/ytai/ioio.git       tags/App-IOIO0330
   - jeromq	https://github.com/zeromq/jeromq.git   77a28b4 -b v_1.7.5
2) Define the top level variable GR_PKGS_ROOT in Eclipse to point where
these repo live locally
