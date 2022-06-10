
## Theoretical Derivation for paper "Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples" on performance analysis

This folder contains the automated theoretical derivation for the paper "Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples" on performance analysis

The files for the automated theory derivation are implemented by **MATLAB**.

Readers can download this folder and directly run **main.m** to obtain the results of Tab. 2,3,4 in the Paper.

### I. The automated derivation script is described as follows:

**a) For Protocols (Tab. 2,3):**
1. tMMA: The Time cost of MMA for A * B, where A with shape n * k and B with shape k * m
2. tMIA: The Time cost of MIA for M with shape n * n
3. tSEM: The Time cost of SEM for A * B, where A with shape n * k and B with shape k * m

**b) For Algorithm (Tab. 4):**
1. tVRGF1: The analysis for Algorithm VRG with role "F1"
2. tVRGF2: The analysis for Algorithm VRG with role "F2"
3. tVRGFC: The analysis for Algorithm VRG with role "C"
4. tVRL2PF1:  The analysis for Algorithm VRL-2P with role "F1"
5. tVRL2PF3:  The analysis for Algorithm VRL-2P with role "F3"
6. tVRL2PFC:  The analysis for Algorithm VRL-2P with role "C"
7. tVRLMPF1:  The analysis for Algorithm VRL-MP with role "F1"
8. tVRLMPFi:  The analysis for Algorithm VRL-MP with role "Fi"
9. tVRLMPFm:  The analysis for Algorithm VRL-MP with role "Fm"
10. tVRLMPFC:  The analysis for Algorithm VRL-MP with role "C"

The analysis results of the above scripts are **all derived from our source code**.

We use comments to mark the exact source of each calculation in the scripts and summarize the key code of each protocol and algorithm in folder **"summaryOfCode"**.

Readers can read our scripts and code summaries to check the correctness of Tab. 2,3,4 in the Paper.

### II. The symbols used in the scripts are described as follows:

1. d_1,d_2,d_i,d_m: $d_1$,$d_2$,$d_i$,$d_m$
2. d_1_is1: $d_{1 \sim i-1}=\sum\nolimits_{t=1}^{i-1}{{{d}_{t}}}$
3. d_1_i: $d_{1 \sim i}=\sum\nolimits_{t=1}^{i}{{{d}_{t}}}$
4. d_1_ms1: $d_{1 \sim m-1}=\sum\nolimits_{t=1}^{m-1}{{{d}_{t}}}$
5. d_1_m: $d_{1 \sim m}=\sum\nolimits_{t=1}^{m}{{{d}_{t}}}$
6. d_2_m: $d_{2 \sim m}=\sum\nolimits_{t=2}^{m}{{{d}_{t}}}$
7. d_i_m: $d_{i \sim m}=\sum\nolimits_{t=i}^{m}{{{d}_{t}}}$
8. d_ia1_m: $d_{i+1 \sim m}=\sum\nolimits_{t=i+1}^{m}{{{d}_{t}}}$
9. d2_1_m: $\sum\nolimits_{t=1}^{m}{{{d}^2_{t}}}$
