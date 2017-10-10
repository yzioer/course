<template>
  <div>
    <h1>Simulation '{{ this.$route.query.sim }}'</h1>
    <div v-if="loading">Loading...</div>
    <div v-if="!loading">This simulation is based on the
      <a :href="simInfo.configurationSourceURL">{{simInfo.configurationName}} configuration</a> and runs for {{simInfo.days}} days.</div>
    <h2>Ranking</h2>
    <p>The ranking is based on an exponentially moving average, measured at the last day of the simulation. If there are multiple instances of an agent type, the type score is the average score of its instances. The number of simulation days may change before the final ranking, so do not bet on it.</p>
    <p>If your team does not appear in the ranking despite having pushed the agent class for the relevant exercise to github, please drop me an e-mail. There might be a bug.</p>
    <table class="agentlist" v-if="!loadingRanking">
      <tr>
        <td>Rank</td>
        <td>Agent</td>
        <td>Utility</td>
        <td>Source</td>
        <td>Version</td>
      </tr>
      <tr v-for="(rank,index) in ranking">
        <td>{{index + 1}}</td>
        <td>{{`${rank.type}`}}</td>
        <td>{{`${rank.averageUtility}`}}</td>
        <td>
          <a :href="`${rank.url}`">source</a>
        </td>
        <td>{{`${rank.version}`}}</td>
      </tr>
    </table>
    <h2>Visualization</h2>
    <ul class="linklist" v-if="!loading">
      <!-- <li>
            // TODO insert proper simulation link on github
            <a :href="simDescription" target="_blank">Description</a>
          </li> -->
      <li>
        <router-link :to="{name: 'trades', query: {sim: this.$route.query.sim, day: 0, selection: 'consumers,firms', step: 1}}">Trade</router-link>
      </li>
    </ul>
    <h2>Data</h2>
    <div class="chart-wrapper">
      <chart :simulationid="simId" :suggestedmetric="datametric" @updateUrl="updateUrl"></chart>
    </div>
  </div>
</template>

<script>
import Chart from '@/components/Chart';
import config from '../config';

export default {
  name: 'simulationview',
  components: {
    Chart,
  },
  data() {
    return {
      loading: true,
      loadingRanking: true,
      simDescription: '',
      simInfo: null,
      ranking: null,
      simId: this.$route.query.sim,
      datametric: this.$route.query.metric ? this.$route.query.metric : 'production',
    };
  },
  methods: {
    updateUrl(selection) {
      this.$router.replace({
        name: 'simulation',
        query: {
          sim: this.simId,
          metric: selection,
        },
      });
      this.datametric = selection;
    },
  },
  created() {
    // get simulation info
    fetch(
      `${config.apiURL}/info?sim=${this.$route.query.sim}`,
      config.xhrConfig,
    )
      .then(config.handleFetchErrors)
      .then(response => response.json())
      .then(
      (response) => {
        this.simDescription = response.name;
        this.simInfo = response;
        this.loading = false;
      },
    )
      .catch(error => config.alertError(error));

    // get simulation ranking
    fetch(
      `${config.apiURL}/ranking?sim=${this.$route.query.sim}`,
      config.xhrConfig,
    )
      .then(config.handleFetchErrors)
      .then(response => response.json())
      .then(
      (response) => {
        this.ranking = response.list;
        this.loadingRanking = false;
      },
    )
      .catch(error => config.alertError(error));
  },
};
</script>

<style lang="sass">
@import '../assets/sass/vars'
@import '../assets/sass/mixins'

.agentlist
  padding: 0

  tr:nth-child(even)
    background-color: #f2f2f2

  th, td
    padding: 10px
    text-align: left

  li
    text-align: left

</style>

