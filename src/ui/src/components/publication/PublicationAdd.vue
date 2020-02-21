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
            <v-form ref="form" v-model="valid">
                <v-file-input ref="file-input"
                              v-model="file"
                              label="Выберите XML-файл с публикацией в формате S1000D 4.1"
                              accept=".xml"
                              :rules="fileRules"
                              show-size
                />
            </v-form>
        </template>

        <template v-slot:mainButton>
            <v-btn v-bind:disabled="loading"
                   v-bind:loading="loading"
                   @click="submitFile">Загрузить</v-btn>
        </template>
    </custom-dialog>
</template>

<script>
    import {mapGetters} from "vuex";
    import CustomDialog from "../customcomponents/CustomDialog";
    import ErrorCodesStrings from "../../utils/ErrorCodes";

    export default {
        name: "PublicationAdd",
        components: {CustomDialog},

        data: () => ({
            valid: false,
            file: null,
            loading: false,
            loadingErrorText: null,
            fileRules: [
                v => v || 'Не выбран файл для загрузки.',
                v => v && v.size < 1048576 || 'Файл с публикацией не должен превышать 1 Мб.'
            ]
        }),

        computed: {
            ...mapGetters('authentication', [
                'isEditor'
            ]),
        },

        methods: {
            submitFile() {
                if (!this.$refs["form"].validate())
                    return;

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
                if(this.$refs["form"])
                    this.$refs["form"].reset();
            },
        },
    }
</script>

<style scoped>

</style>