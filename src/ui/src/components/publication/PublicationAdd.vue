<template>
    <custom-dialog v-if="isEditor"
                   ref="main-dialog"
                   max-width="800px"
                   main-button-caption="Загрузить"
                   form-caption="Загрузить публикацию"
                   v-on:set-start-state="setStartState"
    >
        <template v-slot:alertText>{{loadingErrorText}}</template>

        <template v-slot:mainComponents>
            <v-file-input ref="file-input"
                          v-model="file"
                          label="Выберите XML-файл с публикацией в формате S1000D 4.1"
                          accept=".xml"
                          show-size
            />
        </template>

        <template v-slot:mainButton>
            <v-btn v-bind:disabled="needDisableSubmitButton"
                   v-bind:loading="loading"
                   @click="submitFile">Загрузить</v-btn>
        </template>
    </custom-dialog>
</template>

<script>
    // TODO написать ограничение по величине загружаемого файла
    import {mapGetters} from "vuex";
    import CustomDialog from "../customcomponents/CustomDialog";
    import ErrorCodesStrings from "../../utils/ErrorCodes";

    export default {
        name: "PublicationAdd",
        components: {CustomDialog},

        data: () => ({
            file: null,
            loading: false,
            loadingErrorText: null,
        }),

        computed: {
            needDisableSubmitButton: function () {
                return !this.file || this.loading;
            },

            ...mapGetters('authentication', [
                'isEditor'
            ]),
        },

        methods: {
            submitFile() {
                this.loading = true;
                this.loadingErrorText = null;
                let mainDialog = this.$refs['main-dialog'];
                this.$store.dispatch('publications/add', this.file)
                    .then(() => {
                        mainDialog.closeDialog();
                    })
                    .catch((error) => {
                        if(error.response && error.response.data && error.response.data.errorCode) {
                            this.loadingErrorText = ErrorCodesStrings[error.response.data.errorCode];
                        }
                        else {
                            this.loadingErrorText = 'Не удалось загрузить публикацию.';
                        }
                        mainDialog.showAlert();
                    })
                    .finally(() => this.loading = false);
            },

            setStartState() {
                this.file = null;
            },
        },
    }
</script>

<style scoped>

</style>