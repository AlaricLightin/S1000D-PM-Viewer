<template>
    <custom-dialog v-if="isActive"
                   ref="main-dialog"
                   max-width="300px"
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
            isActive: function () {
                return this.isAdmin();
            }
        },

        methods: {
            deletePublication() {
                let mainDialog = this.$refs['main-dialog'];
                this.$store.dispatch('publications/delete', this.publication.id)
                    .catch(() => mainDialog.showAlert())
            },

            ...mapGetters('authentication', [
                'isAdmin'
            ]),
        }
    }
</script>

<style scoped>

</style>