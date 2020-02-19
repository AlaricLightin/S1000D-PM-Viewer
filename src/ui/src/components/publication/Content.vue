<template>
    <div>
        <v-progress-linear v-if="loading" indeterminate/>
        <loading-error-alert v-if="loadingError"/>

        <v-treeview ref="treeView"
                    open-all
                    :items="items"
        ></v-treeview>
    </div>
</template>

<script>
    import LoadingErrorAlert from "../errors/LoadingErrorAlert";

    export default {
        name: "Content",
        components: {LoadingErrorAlert},
        data: () => ({
            items: [],
            loadingError: false,
            loading: false
        }),

        mounted() {
            this.loadContent();
        },

        methods: {
            loadContent() {
                this.loadingError = false;
                this.loading = true;
                this.$store.getters['authentication/getGetRequest'](`/publication/${this.$route.params.id}/content`)
                    .then(r => {
                        this.items = r.data;
                        this.$refs["treeView"].updateAll(true);
                    })
                    .catch(() => this.loadingError = true)
                    .finally(() => this.loading = false);
            }
        }
    }
</script>

<style scoped>

</style>