<template>
    <custom-dialog v-if="isAdmin"
                   ref="main-dialog"
                   max-width="400px"
                   main-button-caption="Удалить"
                   form-caption="Удаление публикации"
                   v-on:ok-button-click="deletePublication"
    >
        <template v-slot:alertText>Не удалось удалить публикацию.</template>
        <template v-slot:mainComponents><p>Вы действительно хотите удалить публикацию?</p></template>
    </custom-dialog>
</template>

<script>
    import {mapGetters} from "vuex";
    import CustomDialog from "../customcomponents/CustomDialog";

    export default {
        name: "PublicationDelete",
        components: {CustomDialog},
        props: {
            publication: Object
        },

        computed: {
            ...mapGetters('authentication', [
                'isAdmin'
            ]),
        },

        methods: {
            deletePublication() {
                let mainDialog = this.$refs['main-dialog'];
                this.$store.dispatch('publications/delete', this.publication.id)
                    .catch(() => mainDialog.showAlert())
            },
        }
    }
</script>

<style scoped>

</style>